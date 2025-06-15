package cc.unknown.module.impl.combat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.netty.InboundEvent;
import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.client.network.TimedPacket;
import cc.unknown.util.client.system.Clock;
import cc.unknown.value.impl.Slider;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@SuppressWarnings("all")
@ModuleInfo(name = "FakeLag", description = "Bad wifi.", category = Category.COMBAT)
public class FakeLag extends Module {	
	private final Slider minOutgoingDelay = new Slider("MinOutgoingDelay", this, 90, 0, 500, 5);
	private final Slider maxOutgoingDelay = new Slider("MaxOutgoingDelay", this, 100, 0, 500, 5);
	private final Slider minInboundDelay = new Slider("MinInboundDelay", this, 100, 0, 500, 5);
	private final Slider maxInboundDelay = new Slider("MaxInboundDelay", this, 150, 0, 500, 5);

	private final List<Packet<?>> outgoingQueue = new CopyOnWriteArrayList<>();
	private final List<Packet> inboundQueue = new CopyOnWriteArrayList<>();

	private final Clock outgoingClock = new Clock();
	private final Clock inboundClock = new Clock();
	
	@Override
	public void onEnable() {
		if (mc.thePlayer == null) {
			toggle();
			return;
		}

		outgoingQueue.clear();
	}
	
	@Override
	public void guiUpdate() {
		correct(minOutgoingDelay, maxOutgoingDelay);
		correct(minInboundDelay, maxInboundDelay);
	}

	@Override
	public void onDisable() {
		for (Packet packet : outgoingQueue) {
			PacketUtil.sendNoEvent(packet);
		}
		outgoingQueue.clear();
	}
	
	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
		if (event.phase == Phase.START) return;

		if (isEnabled() && mc.thePlayer != null && !mc.thePlayer.isDead) {
			if (inboundClock.reachedSince(minInboundDelay.getAsInt(), maxInboundDelay.getAsInt())) {
				while (!inboundQueue.isEmpty()) {
					PacketUtil.receiveNoEvent(inboundQueue.remove(0));
				}
				inboundClock.reset();
			} else if (outgoingClock.reachedSince(minOutgoingDelay.getAsInt(), maxOutgoingDelay.getAsInt())) {
				while (!outgoingQueue.isEmpty()) {
					Packet packet = inboundQueue.get(0);
					
					if (packet instanceof S32PacketConfirmTransaction) {
						S32PacketConfirmTransaction transaction = (S32PacketConfirmTransaction) packet;
						PacketUtil.sendNoEvent(new C0FPacketConfirmTransaction(transaction.getWindowId(), transaction.getActionNumber(), false));
					}
					
					PacketUtil.sendNoEvent(outgoingQueue.remove(0));
				}
				outgoingClock.reset();
			}
		}
	}

	@SubscribeEvent
	public void onInbound(InboundEvent event) {
		if (minInboundDelay.getAsInt() != 0 || maxInboundDelay.getAsInt() != 0) {
			inboundQueue.add(event.packet);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
		if (minOutgoingDelay.getAsInt() != 0 || maxOutgoingDelay.getAsInt() != 0) {
			outgoingQueue.add(event.packet);
			event.setCanceled(true);
		}
	}
}
