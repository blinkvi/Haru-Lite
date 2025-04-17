package cc.unknown.handlers;

import static cc.unknown.util.render.client.ColorUtil.pink;
import static cc.unknown.util.render.client.ColorUtil.reset;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.player.InboundEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.client.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TransactionHandler implements Accessor {
	
	private static AtomicBoolean toggle = new AtomicBoolean(false);

	public static void start() {
		toggle.set(!toggle.get());
	}
	
	@SubscribeEvent
	public void onInbound(InboundEvent event) {
        final Packet<?> packet = event.getPacket();
        if (!toggle.get()) return;
        
        if (packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            ChatUtil.display(reset + "[" + pink + "*" + reset + "] " + reset + String.format(reset + " (ID: %s) (WindowID: %s)", wrapper.getActionNumber(), wrapper.getWindowId()));
        }
	}
}