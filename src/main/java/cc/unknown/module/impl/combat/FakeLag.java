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
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@SuppressWarnings("all")
@ModuleInfo(name = "FakeLag", description = "Bad wifi.", category = Category.COMBAT)
public class FakeLag extends Module {	
	private Slider minOut = new Slider("MinOutgoingDelay", this, 90, 0, 500, 5);
	private Slider maxOut = new Slider("MaxOutgoingDelay", this, 100, 0, 500, 5);
	private Slider minInb = new Slider("MinInboundDelay", this, 100, 0, 500, 5);
	private Slider maxInb = new Slider("MaxInboundDelay", this, 150, 0, 500, 5);
	private Slider chance = new Slider("Chance", this, 100, 0, 100, 1);
	
	private final List<Packet<?>> out = new CopyOnWriteArrayList<>();
	private final List<Packet> inb = new CopyOnWriteArrayList<>();
	
	private Clock send = new Clock();
	private Clock receive = new Clock();
	
	@Override
	public void onEnable() {
		out.clear();
		correctValues(minOut, maxOut);
		correctValues(minInb, maxInb);
	}

	@Override
	public void onDisable() {
		for (Packet packet : out) {
			ReflectUtil.outboundPacketsQueue().add(ReflectUtil.InboundHandlerTuplePacketListener(packet));
		}
		out.clear();
	}
	
	@SubscribeEvent
	public void onInbound(InboundEvent event) {
		Packet packet = event.packet;
		if (packet instanceof S03PacketTimeUpdate || packet instanceof S02PacketChat || packet instanceof S19PacketEntityStatus) return;
		if (packet instanceof S40PacketDisconnect) toggle();
		
		inb.add(packet);
		event.setCanceled(true);
		
    	if (receive.reachedSince(minInb.getAsInt(), maxInb.getAsInt())) {
    		while (!inb.isEmpty()) {
    			inb.get(0).processPacket(ReflectUtil.packetListener());
    			inb.remove(inb.get(0));
    		}
    		receive.reset();
    	}
	}
	
	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
		Packet packet = event.packet;
		out.add(packet);
		event.setCanceled(true);
		
		if (isEnabled() && mc.thePlayer != null) {
			if (send.reachedSince(minOut.getAsInt(), maxOut.getAsInt())) {
        	    while (!out.isEmpty()) {
        			PacketUtil.sendNoEvent(out.get(0));
        			out.remove(out.get(0));
        	    }
        	    send.reset();
			}
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		correctValues(minOut, maxOut);
		correctValues(minInb, maxInb);
		
		if (event.phase == Phase.START) {
			if (mc.thePlayer == null || mc.thePlayer.isDead) return;
			if (!MathUtil.chance(chance.getValue())) return; 
		}
	}
}
