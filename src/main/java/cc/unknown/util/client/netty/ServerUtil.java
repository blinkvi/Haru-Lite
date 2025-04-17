package cc.unknown.util.client.netty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.system.StringUtil;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class ServerUtil implements Accessor {
    public static String serverName;
    public static String serverAddresses;
    public static Map<String, ServerData> serverDataMap = new HashMap<>();
    
    public static boolean isConnectedToKnownServer(String serverIP) {
        if (serverIP == null) return false;

        try (InputStream inputStream = mc.getResourceManager().getResource(new ResourceLocation("haru/mapping/servers.json")).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Gson gson = new Gson();

            List<ServerData> serverMappings = gson.fromJson(reader, new TypeToken<List<ServerData>>() {

				private static final long serialVersionUID = 1L;
				
            }.getType());

            for (ServerData mapping : serverMappings) {
                ServerData serverData = new ServerData();
                serverData.name = mapping.name;
                serverData.primaryAddress = mapping.primaryAddress;

                serverDataMap.put(mapping.primaryAddress.toLowerCase(), serverData);

                for (String address : mapping.addresses) {
                    serverDataMap.put(address.toLowerCase(), serverData);
                }
            }
        } catch (Exception e) {
            return false;
        }

        serverIP = serverIP.toLowerCase();
        serverAddresses = serverIP;
        ServerData serverData = serverDataMap.get(serverIP);

        if (serverData != null) {
            serverName = serverData.name;
            return true;
        }

        for (Map.Entry<String, ServerData> entry : serverDataMap.entrySet()) {
            String knownAddress = entry.getKey();
            if (serverIP.endsWith(knownAddress)) {
                serverData = entry.getValue();
                serverName = serverData.name;
                return true;
            }
        }

        return false;
    }
    
    public static String getDetectedGame(Scoreboard scoreboard) {
        String[] games = {"BedWars", "SkyWars", "Skywars Speed", "TNTTag", "ArenaPvP"};
        return scoreboard.getScoreObjectives().stream().map(obj -> obj.getDisplayName().replaceAll("§[0-9A-FK-ORa-fk-or]", "")).filter(name -> StringUtil.containsAny(name, games)).findFirst().orElse("Unknown");
    }
}
