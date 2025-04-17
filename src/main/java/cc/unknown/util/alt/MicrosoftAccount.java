package cc.unknown.util.alt;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

public class MicrosoftAccount extends Account {
    private String refreshToken;

    public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public MicrosoftAccount(String name, String uuid, String accessToken, String refreshToken) {
        super(name, uuid, accessToken);
        this.refreshToken = refreshToken;
    }

    public static MicrosoftAccount create() {
        MicrosoftAccount account = new MicrosoftAccount("", "", "", "");
        Consumer<String> consumer = (String refreshToken) -> {
            account.setRefreshToken(refreshToken);
            account.login();
        };

        MicrosoftLogin.getRefreshToken(consumer);
        return account;
    }

    @Override
    public boolean login() {
        if (refreshToken.isEmpty()) return super.login();

        LoginData loginData = MicrosoftLogin.login(refreshToken);
        if (!loginData.isGood()) {
            return false;
        }
        
        this.setName(loginData.username);
        this.setUuid(loginData.uuid);
        this.setAccessToken(loginData.mcToken);
        this.setRefreshToken(loginData.newRefreshToken);
        return super.login();
    }

    @Override
    public boolean isValid() {
        return super.isValid() && !refreshToken.isEmpty();
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = super.toJson();
        object.addProperty("refreshToken", refreshToken);
        return object;
    }

    @Override
    public void parseJson(JsonObject object) {
        super.parseJson(object);

        if (object.has("refreshToken")) {
            refreshToken = object.get("refreshToken").getAsString();
        }
    }
}