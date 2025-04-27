package cc.unknown.socket.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.WebSocketCore;
import cc.unknown.util.client.system.LocalDateTimeStructuredAdapter;
import net.dv8tion.jda.api.entities.Message;

public class CosmeticSocket extends WebSocketCore {
	public static List<SuperCosmetic> cosmeticList = new ArrayList<>();
	public static Message latestChatMessage;
	
	private static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeStructuredAdapter()).create();
	
	public static void tick(SuperCosmetic superCosmetic) {
	    getCosmeticChannel().getHistory().retrievePast(30).queue(messages -> {
	        
	        for (Message msg : messages) {
	            if (msg.getAuthor().getId().equals(getBotID())) {
	                String content = msg.getContentRaw();

	                if (content.startsWith("[") && content.endsWith("]")) {
	                    SuperCosmetic[] cosmetics = gson.fromJson(content, SuperCosmetic[].class);
	                    cosmeticList.clear();
	                    cosmeticList.addAll(Arrays.asList(cosmetics));
	                    break;
	                }
	            }
	        }

	        cosmeticList.removeIf(cosmetic -> 
	            cosmetic.getLastUpdated().isBefore(LocalDateTime.now().minusHours(4)) || 
	            cosmetic.getName().equalsIgnoreCase(superCosmetic.getName())
	        );

	        cosmeticList.add(superCosmetic);

	        String json = gson.toJson(cosmeticList);

	        boolean isDuplicate = false;
	        for (Message msg : messages) {
	            if (msg.getAuthor().getId().equals(getBotID()) && msg.getContentRaw().equals(json)) {
	                isDuplicate = true;
	                break;
	            }
	        }

	        if (!isDuplicate) {
	            Message lastMessage = null;
	            for (Message msg : messages) {
	                if (msg.getAuthor().getId().equals(getBotID())) {
	                    lastMessage = msg;
	                    break;
	                }
	            }

	            if (lastMessage != null) {
	                lastMessage.editMessage(json).queue();
	            } else {
	                getCosmeticChannel().sendMessage(json).queue();
	            }
	        }
	    });
	}

}
