package cc.unknown.handlers;

import static cc.unknown.util.render.client.ColorUtil.pink;
import static cc.unknown.util.render.client.ColorUtil.reset;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.InboundEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.client.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class TransactionHandler implements Accessor {
	
	private static AtomicBoolean toggle = new AtomicBoolean(false);

	public static void start() {
		toggle.set(!toggle.get());
	}
	
	@EventLink
	public final Listener<InboundEvent> onInbound = event -> {
        final Packet<?> packet = event.packet;
        if (!toggle.get()) return;
        
        if (packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            ChatUtil.display(reset + "[" + pink + "*" + reset + "] " + reset + String.format(reset + " (ID: %s) (WindowID: %s)", wrapper.getActionNumber(), wrapper.getWindowId()));
        }
	};
}