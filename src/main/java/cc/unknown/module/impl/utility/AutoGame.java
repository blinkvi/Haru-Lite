package cc.unknown.module.impl.utility;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.ServerUtil;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.value.impl.Mode;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoGame", description = "Automatically performs certain actions without user input.", category = Category.UTILITY)
public class AutoGame extends Module {
	
	private final Mode mode = new Mode("Mode", this, "Universocraft", "Universocraft");
	private Map<String, String> uniCommands = new HashMap<>();
	
	@SubscribeEvent
	public void onInbound(ClientChatReceivedEvent event) {
	    if (!isInGame()) return;
	    
        String receiveMessage = event.message.getUnformattedText();
        String game = ServerUtil.getDetectedGame(mc.theWorld.getScoreboard());

        if (mode.is("Universocraft")) {
            uniCommands.put("ArenaPvP", "/leave");
            uniCommands.put("BedWars", "/bw random");
            uniCommands.put("TNTTag", "/playagain");
            uniCommands.put("SkyWars", "/sw random");
            uniCommands.put("SkyWars Speed", "/sw random");

            if (uniCommands.containsKey(game)) {
                if (shouldSendCommand(game, receiveMessage)) {
                    String command = uniCommands.get(game);
                    ChatUtil.chat(command);
                }
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
