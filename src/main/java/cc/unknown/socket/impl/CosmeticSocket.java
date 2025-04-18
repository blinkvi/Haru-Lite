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
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;

@UtilityClass
public class CosmeticSocket extends WebSocketCore {
	public List<SuperCosmetic> cosmeticList = new ArrayList<>();
	public Message latestChatMessage;
	
	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeStructuredAdapter()).create();

	public void tick(SuperCosmetic superCosmetic) {
	    getCosmeticChannel().getHistory().retrievePast(30).queue(messages -> {
	        latestChatMessage = null;

	        for (Message msg : messages) {
	            if (msg.getAuthor().getId().equals(getBotID())) {
	                String content = msg.getContentRaw();

	                if (content.startsWith("[") && content.endsWith("]")) {
	                    latestChatMessage = msg;

	                    SuperCosmetic[] cosmetics = gson.fromJson(content, SuperCosmetic[].class);
	                    cosmeticList.clear();
	                    cosmeticList.addAll(Arrays.asList(cosmetics));
	                    break;
	                }
	            }
	        }

	        cosmeticList.removeIf(cosmetic ->
	            !cosmetic.getName().equalsIgnoreCase(superCosmetic.getName()) &&
	            cosmetic.getLastUpdated().isBefore(LocalDateTime.now().minusHours(4))
	        );

	        cosmeticList.removeIf(cosmetic ->
	            cosmetic.getName().equalsIgnoreCase(superCosmetic.getName())
	        );

	        cosmeticList.add(superCosmetic);

	        String json = gson.toJson(cosmeticList);

	        if (latestChatMessage != null) {
	            latestChatMessage.editMessage(json).queue();
	        } else {
	            getCosmeticChannel().sendMessage(json).queue();
	        }
	    });
	}
}
