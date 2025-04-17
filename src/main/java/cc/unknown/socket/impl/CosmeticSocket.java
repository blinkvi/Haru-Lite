package cc.unknown.socket.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.WebSocketCore;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CosmeticSocket extends WebSocketCore {
	public static List<SuperCosmetic> cosmeticList = new ArrayList<>();
	public static Message latestChatMessage = null;


	public static void tick(SuperCosmetic superCosmetic){
		WebSocketCore.getCosmeticChannel().getHistory().retrievePast(30).queue(messages -> {
			latestChatMessage = null;
			for (Message msg : messages) {
				//System.out.println(msg.getContentRaw());
				if (msg.getAuthor().getId().equals(WebSocketCore.getBotID())) {
					String content = msg.getContentRaw();
					if (content.startsWith("[") && content.endsWith("]")) {
						latestChatMessage = msg;
					}

					Gson gson = new Gson();

					//chatgpt
					SuperCosmetic[] cosmetics = gson.fromJson(content, SuperCosmetic[].class);

					cosmeticList.clear();
					Collections.addAll(cosmeticList, cosmetics);

				}
			}

			cosmeticList.removeIf(existing -> existing.getName().equalsIgnoreCase(superCosmetic.getName()));
			cosmeticList.add(superCosmetic);

			Gson gson = new Gson();
			String json = gson.toJson(cosmeticList);

			latestChatMessage.editMessage(json).queue();
		});
	}
}
