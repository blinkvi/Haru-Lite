package cc.unknown.module.impl.combat;

import java.awt.Color;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.event.netty.InboundEvent;
import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.mixin.interfaces.IC02PacketUseEntity;
import cc.unknown.mixin.interfaces.IS14PacketEntity;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.client.network.TimedPacket;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@SuppressWarnings("all")
@ModuleInfo(name = "BackTrack", description = "Allows you to hit entities at their past positions.", category = Category.COMBAT)
public class BackTrack extends Module {

	private Slider delay = new Slider("Delay", this, 90, 1, 200, 1);
	private Slider distance = new Slider("Distance", this, 6, 3.1, 6, 0.01);
	private Bool onlyCombat = new Bool("OnlyCombat", this, true);
	private Bool renderPosition = new Bool("RenderPosition", this, true);

	private Slider red = new Slider("Red", this, 255, 0, 255, 1, renderPosition::get);
	private Slider green = new Slider("Green", this, 0, 0, 255, 1, renderPosition::get);
	private Slider blue = new Slider("Blue", this, 0, 0, 255, 1, renderPosition::get);
	
	public final MultiBool disableOn = new MultiBool("DisableOn", this, Arrays.asList(
			new Bool("WorldChange", false),
			new Bool("Disconnect", false), 
			new Bool("ReceiveDamage", false)));

	private Queue<TimedPacket> queue = new ConcurrentLinkedQueue<>();
	private Vec3 vec3;
	private Vec3 lastVec3;
	private EntityPlayer target;
	private int attackTicks;

	@Override
	public void onEnable() {
		if (mc.thePlayer == null) {
			toggle();
			return;
		}
		
		queue.clear();
		vec3 = lastVec3 = null;
		target = null;
	}

	@Override
	public void onDisable() {
		if (mc.thePlayer == null) return;
		if (mc.thePlayer != null && !queue.isEmpty()) {
			queue.forEach(e -> PacketUtil.receiveNoEvent(e.packet));
		}
		queue.clear();
	}
	
	@SubscribeEvent
	public void onPreUpdate(PreUpdateEvent event) {
		try {
			attackTicks++;
			if (attackTicks > 7 || vec3.distanceTo(mc.thePlayer.getPositionVector()) > distance.getValue()) {
				target = null;
				vec3 = lastVec3 = null;
			} 
			lastVec3 = vec3;
		} catch (NullPointerException e) {
			
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.phase == Phase.END) return;
		
		while(!queue.isEmpty() && queue.peek().clock.reached(delay.getAsInt())) {
			Packet packet = queue.poll().packet;
			PacketUtil.receiveNoEvent(packet);
		}
		
		if (queue.isEmpty() && target != null) {
			vec3 = target.getPositionVector();
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if (target == null) return;
		
		if (renderPosition.get())
			RenderUtil.drawBox(target, vec3, lastVec3, new Color(red.getAsInt(), green.getAsInt(), blue.getAsInt()));
	}
	
	@SubscribeEvent
	public void onInbound(InboundEvent event) {
		try {
			Packet packet = event.packet;
			EntityPlayerSP player = mc.thePlayer;
			
			if (disableOn.isEnabled("ReceiveDamage") && player.getHealth() < player.getMaxHealth() && player.hurtTime != 0) {
				release();
				return;
			}
			
			if (disableOn.isEnabled("Disconnect") && packet instanceof S40PacketDisconnect) {
				release();
				toggle();
				return;
			}
			
			if (player == null || player.ticksExisted < 20) {
				queue.clear();
				return;
			}
			
			if (target == null) {
				release();
				return;
			}
			
			if (event.isCanceled()) return;
			if (packet instanceof S19PacketEntityStatus || packet instanceof S02PacketChat) return;
			if (packet instanceof S08PacketPlayerPosLook) {
				release();
				target = null;
				vec3 = lastVec3 = null;
				return;
			}
			
			if (packet instanceof S13PacketDestroyEntities) {
				S13PacketDestroyEntities wrapper = (S13PacketDestroyEntities) packet;
				for (int id : wrapper.getEntityIDs()) {
					if (id == target.getEntityId()) {
						target = null;
						vec3 = lastVec3 = null;
						release();
						return;
					}
				}
			} else if (packet instanceof S14PacketEntity) {
				S14PacketEntity wrapper = (S14PacketEntity) packet;
				if (((IS14PacketEntity)wrapper).getEntityId() == target.getEntityId())
					vec3 = new Vec3(wrapper.func_149062_c() / 32, wrapper.func_149061_d() / 32, wrapper.func_149064_e() / 32);
			} else if (packet instanceof S18PacketEntityTeleport) {
				S18PacketEntityTeleport wrapper = (S18PacketEntityTeleport) packet;
				if (wrapper.getEntityId() == target.getEntityId())
					vec3 = new Vec3(wrapper.getX() / 32, wrapper.getY() / 32, wrapper.getZ() / 32);
			}
			
			queue.add(new TimedPacket(packet));
			event.setCanceled(true);
		} catch (NullPointerException e) {
			//e.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
		Packet packet = event.packet;
		if (packet instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;
			if (onlyCombat.get() && wrapper.getAction() != C02PacketUseEntity.Action.ATTACK) return;
			
			try {
				attackTicks = 0;
				EntityPlayer entity = (EntityPlayer) wrapper.getEntityFromWorld(mc.theWorld);
				if (target != null && ((IC02PacketUseEntity)wrapper).getEntityId() == target.getEntityId()) return;
				target = entity;
				vec3 = lastVec3 = entity.getPositionVector();
			} catch (ClassCastException e) {
				//e.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void onLoadWorld(WorldEvent.Load event) {
		if (disableOn.isEnabled("WorldChange")) {
			toggle();
		}
	}

	private void release() {
		if (!queue.isEmpty()) {
			queue.forEach(e -> PacketUtil.receiveNoEvent(e.packet));
			queue.clear();
		} 
	}
}
