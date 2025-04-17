package cc.unknown.handlers;

import java.util.Arrays;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.player.OutgoingEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommandHandler {
	
	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
	    if (event.getPacket() instanceof C01PacketChatMessage) {
	        C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
	        String message = packet.getMessage();

	        if (message.startsWith(".")) {
	            event.setCanceled(true);

	            String[] args = message.substring(1).split(" ");
	            if (args.length > 0) {
	                for (Command c : Haru.instance.getCmdManager().getCommands()) {
	                    if (args[0].equalsIgnoreCase(c.getPrefix())) {
	                        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
	                        c.execute(commandArgs);
	                        return;
	                    }
	                }
	            }
	        }
	    }
	}
}
