package cc.unknown.handlers;

import java.util.Arrays;

import cc.unknown.Haru;
import cc.unknown.event.netty.OutgoingEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommandHandler {
	
	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
	    if (event.packet instanceof C01PacketChatMessage) {
	        C01PacketChatMessage packet = (C01PacketChatMessage) event.packet;
	        String message = packet.getMessage();

	        if (message.startsWith(".")) {
	            event.setCanceled(true);

	            String[] args = message.substring(1).split(" ");
	            if (args.length > 0) {
	                Haru.instance.getCmdManager().getCommands().stream()
	                    .filter(c -> args[0].equalsIgnoreCase(c.prefix()))
	                    .findFirst()
	                    .ifPresent(c -> {
	                        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
	                        c.execute(commandArgs);
	                    });
	            }
	        }
	    }
	}
}
