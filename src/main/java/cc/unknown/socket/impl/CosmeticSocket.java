package cc.unknown.socket.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.WebSocketCore;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;

@UtilityClass
public class CosmeticSocket extends WebSocketCore {
	public List<SuperCosmetic> cosmeticList = new ArrayList<>();
	public Message latestChatMessage = null;

	public void tick(SuperCosmetic superCosmetic){

		if(getCosmeticChannel().getHistory().isEmpty()){
			getCosmeticChannel().sendMessage("[]").queue();
		}

		getCosmeticChannel().getHistory().retrievePast(30).queue(messages -> {

			latestChatMessage = null;
			for (Message msg : messages) {
				if (msg.getAuthor().getId().equals(getBotID())) {
					String content = msg.getContentRaw();
					if (content.startsWith("[") && content.endsWith("]")) {
						latestChatMessage = msg;
					}

					Gson gson = new Gson();

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
