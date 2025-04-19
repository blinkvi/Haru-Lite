package cc.unknown.module.impl.utility;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.ServerUtil;
import cc.unknown.util.render.client.ChatUtil;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoGame", description = "Automatically performs certain actions without user input.", category = Category.UTILITY)
public class AutoGame extends Module {
	
	@SubscribeEvent
	public void onInbound(ClientChatReceivedEvent event) {
	    if (!isInGame()) return;
	    
        String receiveMessage = event.message.getUnformattedText();
        String game = ServerUtil.getDetectedGame(mc.theWorld.getScoreboard());

        Map<String, String> gameCommands = new HashMap<>();
        gameCommands.put("ArenaPvP", "/leave");
        gameCommands.put("BedWars", "/bw random");
        gameCommands.put("TNTTag", "/playagain");
        gameCommands.put("SkyWars", "/sw random");
        gameCommands.put("SkyWars Speed", "/sw random");

        if (gameCommands.containsKey(game)) {
            if (shouldSendCommand(game, receiveMessage)) {
                String command = gameCommands.get(game);
                ChatUtil.chat(command);
            }
        }
	}

	private boolean shouldSendCommand(String game, String message) {
	    switch (game) {
	        case "ArenaPvP":
	            return message.contains("Información");

	        case "BedWars":
	        case "TNTTag":
	        case "SkyWars":
	        case "SkyWars Speed":
	            return message.contains("Jugar de nuevo")
	                || message.contains("ha ganado");

	        default:
	            return false;
	    }
	}


}
