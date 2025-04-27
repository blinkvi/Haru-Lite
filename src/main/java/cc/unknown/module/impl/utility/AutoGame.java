package cc.unknown.module.impl.utility;

import java.util.HashMap;
import java.util.Map;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.InboundEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.ServerUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.client.ChatUtil;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(name = "AutoGame", description = "Automatically performs certain actions without user input.", category = Category.UTILITY)
public class AutoGame extends Module {

    public String customCommand = "";
    public String customGame = "";
    
    @EventLink
    public final Listener<InboundEvent> onInbound = event -> {
        if (!PlayerUtil.isInGame()) return;
        
        if (event.packet instanceof S02PacketChat) {
        	S02PacketChat wrapper = (S02PacketChat) event.packet;
        	
            String receiveMessage = wrapper.getChatComponent().getUnformattedText();
            String game = ServerUtil.getDetectedGame(mc.theWorld.getScoreboard());

            Map<String, String> gameCommands = new HashMap<>();
            gameCommands.put("ArenaPvP", "/leave");
            gameCommands.put("BedWars", "/bw random");
            gameCommands.put("TNTTag", "/playagain");
            gameCommands.put("SkyWars", "/sw random");
            gameCommands.put("SkyWars Speed", "/sw random");

            if (!customGame.isEmpty() && !customCommand.isEmpty()) {
                gameCommands.put(customGame, customCommand);
            }

            if (gameCommands.containsKey(game)) {
                if (shouldSendCommand(game, receiveMessage)) {
                    String command = gameCommands.get(game);
                    ChatUtil.chat(command);
                }
            }
        }
    };

    private boolean shouldSendCommand(String game, String message) {
        Map<String, String> gameConditions = new HashMap<>();
        gameConditions.put("ArenaPvP", "Información");
        gameConditions.put("BedWars", "Jugar de nuevo");
        gameConditions.put("TNTTag", "Jugar de nuevo");
        gameConditions.put("SkyWars", "Jugar de nuevo");
        gameConditions.put("SkyWars Speed", "Jugar de nuevo");

        if (gameConditions.containsKey(game) && message.contains(gameConditions.get(game))) {
            return true;
        }

        if (game.equals(customGame) && message.contains(customCommand)) {
            return true;
        }

        return false;
    }
}