package cc.unknown.util.alt;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cc.unknown.ui.menu.AltManager;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.netty.NetworkUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.structure.comparators.ValuePair;

public class MicrosoftLogin implements Accessor {

    private final static String CLIENT_ID = "ba89e6e0-8490-4a26-8746-f389a0d3ccc7";
	private static final String CLIENT_SECRET = "hlQ8Q~33jTRilP4yE-UtuOt9wG.ZFLqq6pErIa2B";
    private final static int PORT = 8247;

    private static Object httpServer;
    private static Consumer<String> callback;

    private static void browse(final String url) {
    	try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
    }

    public static void getRefreshToken(final Consumer<String> callback) {
        MicrosoftLogin.callback = callback;

        startServer();
        browse("https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&response_type=code&redirect_uri=http://localhost:" + PORT + "&scope=XboxLive.signin%20offline_access&prompt=select_account");
    }

    private final static Gson gson = new Gson();

    public static LoginData login(String refreshToken) {
        // Refresh access token
        final AuthTokenResponse res = gson.fromJson(
                Browser.postExternal("https://login.live.com/oauth20_token.srf", "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&refresh_token=" + refreshToken + "&grant_type=refresh_token&redirect_uri=http://localhost:" + PORT + "&prompt=select_account", false),
                AuthTokenResponse.class
        );

        if (res == null) return new LoginData();

        final String accessToken = res.access_token;
        refreshToken = res.refresh_token;

        // XBL
        final XblXstsResponse xblRes = gson.fromJson(
                Browser.postExternal("https://user.auth.xboxlive.com/user/authenticate",
                        "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=" + accessToken + "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}", true),
                XblXstsResponse.class);

        if (xblRes == null) return new LoginData();

        // XSTS
        final XblXstsResponse xstsRes = gson.fromJson(
                Browser.postExternal("https://xsts.auth.xboxlive.com/xsts/authorize",
                        "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" + xblRes.Token + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}", true),
                XblXstsResponse.class);

        if (xstsRes == null) return new LoginData();

        // Minecraft
        final McResponse mcRes = gson.fromJson(
                Browser.postExternal("https://api.minecraftservices.com/authentication/login_with_xbox",
                        "{\"identityToken\":\"XBL3.0 x=" + xblRes.DisplayClaims.xui[0].uhs + ";" + xstsRes.Token + "\"}", true),
                McResponse.class);

        if (mcRes == null) return new LoginData();

        // Profile
        final ProfileResponse profileRes = gson.fromJson(
                Browser.getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token),
                ProfileResponse.class);

        if (profileRes == null) return new LoginData();

        return new LoginData(mcRes.access_token, refreshToken, profileRes.id, profileRes.name);
    }

    public static void startServer() {
        try {
            Class<?> httpServerClass = Class.forName("com.sun.net.httpserver.HttpServer");
            Method createMethod = httpServerClass.getDeclaredMethod("create", InetSocketAddress.class, int.class);
            httpServer = createMethod.invoke(null, new InetSocketAddress(PORT), 0);

            Class<?> httpHandlerClass = Class.forName("com.sun.net.httpserver.HttpHandler");
            Method createContextMethod = httpServerClass.getMethod("createContext", String.class, httpHandlerClass);

            Object handler = Proxy.newProxyInstance(
                    httpHandlerClass.getClassLoader(),
                    new Class<?>[]{httpHandlerClass},
                    (proxy, method, args) -> {
                        if ("handle".equals(method.getName())) {
                            handleRequest(args[0]);
                        }
                        return null;
                    });

            createContextMethod.invoke(httpServer, "/", handler);

            Method startMethod = httpServerClass.getMethod("start");
            startMethod.invoke(httpServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() throws Exception {
    	if (httpServer != null) {
    		Method stopMethod = httpServer.getClass().getMethod("stop", int.class);
    		stopMethod.invoke(httpServer, 0);
    	}
    }
    
    private static void handleRequest(Object httpExchange) {
        try {
            Class<?> exchangeClass = httpExchange.getClass();

            Method getRequestMethod = exchangeClass.getDeclaredMethod("getRequestMethod");
            getRequestMethod.setAccessible(true);

            Method getRequestURI = exchangeClass.getDeclaredMethod("getRequestURI");
            getRequestURI.setAccessible(true);

            Method getResponseHeaders = exchangeClass.getDeclaredMethod("getResponseHeaders");
            getResponseHeaders.setAccessible(true);

            Method sendResponseHeaders = exchangeClass.getDeclaredMethod("sendResponseHeaders", int.class, long.class);
            sendResponseHeaders.setAccessible(true);

            Method getResponseBody = exchangeClass.getDeclaredMethod("getResponseBody");
            getResponseBody.setAccessible(true);

            String method = (String) getRequestMethod.invoke(httpExchange);

            if ("GET".equalsIgnoreCase(method)) {
                URI requestURI = (URI) getRequestURI.invoke(httpExchange);
            	String queryString = requestURI.getQuery();
            	List<ValuePair> queryParameters = NetworkUtil.parse(queryString, StandardCharsets.UTF_8);

                boolean ok = false;
                for (ValuePair pair : queryParameters) {
                    if ("code".equals(pair.getName())) {
                        handleCode(pair.getValue());
                        ok = true;
                        
                        AltManager.status = "Logeado como " + ColorUtil.green + mc.getSession().getUsername();
                        break;
                    }
                }

                if (!ok) {
                    writeText(httpExchange, "Cannot authenticate.");
                } else {
                	writeText(httpExchange, "<html>\n"
                			+ "    <body>\n"
                			+ "        <p>Authentication completed.</p>\n"
                			+ "        <script>\n"
                			+ "            alert(\"You may now close this tab.\");\n"
                			+ "        </script>\n"
                			+ "    </body>\n"
                			+ "</html>");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeText(Object httpExchange, String text) throws Exception {
    	Class<?> exchangeClass = httpExchange.getClass();
    	Method getResponseHeaders = exchangeClass.getDeclaredMethod("getResponseHeaders");
    	getResponseHeaders.setAccessible(true);
    	Method sendResponseHeaders = exchangeClass.getDeclaredMethod("sendResponseHeaders", int.class, long.class);
    	sendResponseHeaders.setAccessible(true);
    	Method getResponseBody = exchangeClass.getDeclaredMethod("getResponseBody");
    	getResponseBody.setAccessible(true);
    	Object headers = getResponseHeaders.invoke(httpExchange);
    	Method addHeader = headers.getClass().getMethod("add", String.class, String.class);
    	addHeader.invoke(headers, "Content-Type", "text/html; charset=utf-8");
    	sendResponseHeaders.invoke(httpExchange, 200, (long) text.length());
    	OutputStream out = (OutputStream) getResponseBody.invoke(httpExchange);
    	out.write(text.getBytes(StandardCharsets.UTF_8));
    	out.flush();
    	out.close();
    }
    
    private static void handleCode(final String code) {
        try {
            String response = Browser.postExternal(
                    "https://login.live.com/oauth20_token.srf",
                    "client_id=" + CLIENT_ID + "&code=" + code + "&client_secret=" + CLIENT_SECRET + "&grant_type=authorization_code&redirect_uri=http://localhost:" + PORT,
                    false
            );

            AuthTokenResponse res = gson.fromJson(response, AuthTokenResponse.class);
            if (res == null) callback.accept(null);
            else callback.accept(res.refresh_token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AuthTokenResponse {
        @Expose
        @SerializedName("access_token")
        public String access_token;
        @Expose
        @SerializedName("refresh_token")
        public String refresh_token;
    }

    private class XblXstsResponse {
        @Expose
        @SerializedName("Token")
        public String Token;
        @Expose
        @SerializedName("DisplayClaims")
        public DisplayClaims DisplayClaims;

        private class DisplayClaims {
            @Expose
            @SerializedName("xui")
            private Claim[] xui;

            private class Claim {
                @Expose
                @SerializedName("uhs")
                private String uhs;
            }
        }
    }

    public class McResponse {
        @Expose
        @SerializedName("access_token")
        public String access_token;
    }

    public class ProfileResponse {
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("name")
        public String name;
    }
}