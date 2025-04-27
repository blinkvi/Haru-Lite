package cc.unknown.handlers;

import java.util.Arrays;

import cc.unknown.Haru;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.OutgoingEvent;
import cc.unknown.util.Managers;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class CommandHandler implements Managers {

	@EventLink
	public final Listener<OutgoingEvent> onOutgoing = event -> {
	    if (event.packet instanceof C01PacketChatMessage) {
	        C01PacketChatMessage packet = (C01PacketChatMessage) event.packet;
	        String message = packet.getMessage();

	        if (message.startsWith(".")) {
	            event.setCanceled(true);

	            String[] args = message.substring(1).split(" ");
	            if (args.length > 0) {
	                Haru.comMngr.getCommands().stream()
	                    .filter(c -> args[0].equalsIgnoreCase(c.getName()))
	                    .findFirst()
	                    .ifPresent(c -> {
	                        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
	                        c.execute(commandArgs);
	                    });
	            }
	        }
	    }
	};
}
