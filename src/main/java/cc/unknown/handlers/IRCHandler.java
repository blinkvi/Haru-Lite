package cc.unknown.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.Haru;
import cc.unknown.event.player.OutgoingEvent;
import cc.unknown.socket.impl.IRCSocket;
import cc.unknown.util.Accessor;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IRCHandler implements Accessor {
    
    private final List<String> blockWords = Arrays.asList("/", ".", "@here", "@everyone");
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final IRCSocket irc = new IRCSocket();
    private final String prefix = "#";

    private final Map<String, Long> timestamps = new HashMap<>();
    private final long cooldownMillis = 3000;
    
    public IRCHandler() {
    	irc.init();
    	Haru.instance.getLogger().info("IRC initialized.");
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
	public void onOutgoing(OutgoingEvent event) {
	    if (event.packet instanceof C01PacketChatMessage) {
	        C01PacketChatMessage packet = (C01PacketChatMessage) event.packet;
	        String message = packet.getMessage();

	        if (message.startsWith(prefix) && message.length() > 1) {
	            event.setCanceled(true);
	            message = message.substring(1);
	            message = StringUtils.normalizeSpace(message);
	            
	            if (!isBlocked(message) && canSendMessage(message)) {
	                String finalMessage = message;
	                executor.execute(() -> irc.sendMessage(" ``" + finalMessage + "``"));
	                timestamps.put(finalMessage, System.currentTimeMillis());
	            }
	        }
	    }
    }
    
    private boolean isBlocked(String message) {
        for (String word : blockWords) {
            if (message.startsWith(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean canSendMessage(String message) {
        long currentTime = System.currentTimeMillis();
        return !timestamps.containsKey(message) || (currentTime - timestamps.get(message)) >= cooldownMillis;
    }
}
