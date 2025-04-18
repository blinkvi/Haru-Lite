package cc.unknown.module.impl.utility;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.player.InboundEvent;
import cc.unknown.event.player.OutgoingEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.netty.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "Blink", description = "Fakes internet lag", category = Category.UTILITY)
public class Blink extends Module {

	private final List<Packet<?>> packets = new CopyOnWriteArrayList<>();

	@Override
	public void onEnable() {
		packets.clear();
	}

	@Override
	public void onDisable() {
		for (Packet<?> packet : packets) {
			PacketUtil.sendNoEvent(packet);			
		}
		
		packets.clear();
	}

	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
    	if (event.phase == Phase.END) return;
		if (mc.thePlayer == null) return;
		while (!packets.isEmpty()) {
			Packet<?> packet = packets.get(0);

			if (packet instanceof S32PacketConfirmTransaction) {
				S32PacketConfirmTransaction transaction = (S32PacketConfirmTransaction) packet;
				PacketUtil.sendNoEvent(new C0FPacketConfirmTransaction(transaction.getWindowId(), transaction.getActionNumber(), false));
			} else if (packet instanceof S00PacketKeepAlive) {
				S00PacketKeepAlive keepAlive = (S00PacketKeepAlive) packet;
				PacketUtil.sendNoEvent(new C00PacketKeepAlive(keepAlive.func_149134_c()));
			} else if (packet instanceof C03PacketPlayer) {
				break;
			}

			PacketUtil.sendNoEvent(packets.get(0));
			packets.remove(packets.get(0));
		}
	}

	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
		packets.add(event.getPacket());
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onInbound(InboundEvent event) {
		if (event.getPacket() instanceof S18PacketEntityTeleport || event.getPacket() instanceof S14PacketEntity
				|| event.getPacket() instanceof S14PacketEntity.S15PacketEntityRelMove
				|| event.getPacket() instanceof S14PacketEntity.S16PacketEntityLook
				|| event.getPacket() instanceof S14PacketEntity.S17PacketEntityLookMove) {
			return;
		}
	}
}
