package cc.unknown.module.impl.utility;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "PingSpoof", description = "", category = Category.UTILITY)
public class PingSpoof extends Module {

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
	public void onOutgoing(OutgoingEvent event) {
		if (event.packet instanceof C00PacketKeepAlive) {
			event.setCanceled(true);
			packets.add(event.packet);
		}
	}

}
