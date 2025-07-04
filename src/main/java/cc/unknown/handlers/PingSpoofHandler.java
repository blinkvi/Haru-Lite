package cc.unknown.handlers;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.unknown.event.netty.InboundEvent;
import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.event.player.PostPositionEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.client.network.TimedPacket;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.structure.list.Tuple;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("all")
public final class PingSpoofHandler implements Accessor {
    public static ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue<>();
    static Clock enabledTimer = new Clock();
    public static boolean enabled;
    static long amount;
    
	static Tuple<Class[], Boolean> regular = new Tuple<>(new Class[]{C0FPacketConfirmTransaction.class, C00PacketKeepAlive.class, S1CPacketEntityMetadata.class}, false);
    static Tuple<Class[], Boolean> velocity = new Tuple<>(new Class[]{S12PacketEntityVelocity.class, S27PacketExplosion.class}, false);
    static Tuple<Class[], Boolean> teleports = new Tuple<>(new Class[]{S08PacketPlayerPosLook.class, S39PacketPlayerAbilities.class, S09PacketHeldItemChange.class}, false);
    static Tuple<Class[], Boolean> players = new Tuple<>(new Class[]{S13PacketDestroyEntities.class, S14PacketEntity.class, S14PacketEntity.S16PacketEntityLook.class, S14PacketEntity.S15PacketEntityRelMove.class, S14PacketEntity.S17PacketEntityLookMove.class, S18PacketEntityTeleport.class, S20PacketEntityProperties.class, S19PacketEntityHeadLook.class}, false);
    static Tuple<Class[], Boolean> blink = new Tuple<>(new Class[]{C02PacketUseEntity.class, C0DPacketCloseWindow.class, C0EPacketClickWindow.class, C0CPacketInput.class, C0BPacketEntityAction.class, C08PacketPlayerBlockPlacement.class, C07PacketPlayerDigging.class, C09PacketHeldItemChange.class, C13PacketPlayerAbilities.class, C15PacketClientSettings.class, C16PacketClientStatus.class, C17PacketCustomPayload.class, C18PacketSpectate.class, C19PacketResourcePackStatus.class, C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class, C0APacketAnimation.class}, false);
    static Tuple<Class[], Boolean> movement = new Tuple<>(new Class[]{C03PacketPlayer.class, C03PacketPlayer.C04PacketPlayerPosition.class, C03PacketPlayer.C05PacketPlayerLook.class, C03PacketPlayer.C06PacketPlayerPosLook.class}, false);

    public static Tuple<Class[], Boolean>[] types = new Tuple[]{regular, velocity, teleports, players, blink, movement};

    @SubscribeEvent
    public void onOutgoing(OutgoingEvent event) {
    	event.setCanceled(onOutgoing(event.packet, event).isCanceled());
    }

    @SubscribeEvent
    public void onInbound(InboundEvent event) {
    	event.setCanceled(onInbound(event.packet, event).isCanceled());
    }
    
    public OutgoingEvent onOutgoing(Packet<?> packet, OutgoingEvent event) {
        if (!event.isCanceled() && enabled && Arrays.stream(types).anyMatch(tuple -> tuple.getSecond() && Arrays.stream(tuple.getFirst()).anyMatch(regularpacket -> regularpacket == packet.getClass()))) {
            event.setCanceled(true);
            packets.add(new TimedPacket(packet));
        }

        return event;
    }
    
    public InboundEvent onInbound(Packet<?> packet, InboundEvent event) {
    	if (!event.isCanceled() && enabled && Arrays.stream(types).anyMatch(tuple -> tuple.getSecond() && Arrays.stream(tuple.getFirst()).anyMatch(regularpacket -> regularpacket == packet.getClass()))) {
    		event.setCanceled(true);
    		packets.add(new TimedPacket(packet));
    	}
    	
    	return event;
    }

    public static void dispatch() {
        if (!packets.isEmpty()) {
            // Stops the packets from being called twice
            boolean enabled = PingSpoofHandler.enabled;
            PingSpoofHandler.enabled = false;
            packets.forEach(timedPacket -> PacketUtil.queue(timedPacket.packet));
            PingSpoofHandler.enabled = enabled;
            packets.clear();
        }
    }

    public static void disable() {
        enabled = false;
        enabledTimer.setStartTime(enabledTimer.getTime() - 999999999);
    }

    @SubscribeEvent
    public void onWorld(WorldEvent event) {
        dispatch();
    }

    @SubscribeEvent
    public void onMotion(PostPositionEvent event) {
        if (!(enabled = !enabledTimer.reached(100) && !(mc.currentScreen instanceof GuiDownloadTerrain))) {
            dispatch();
        } else {
            enabled = false;

            packets.forEach(packet -> {
                if (packet.millis + amount < System.currentTimeMillis()) {
                    PacketUtil.queue(packet.packet);
                    packets.remove(packet);
                }
            });

            enabled = true;
        }
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players) {
        spoof(amount, regular, velocity, teleports, players, false);
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players, boolean blink, boolean movement) {
        enabledTimer.reset();

        PingSpoofHandler.regular.setSecond(regular);
        PingSpoofHandler.velocity.setSecond(velocity);
        PingSpoofHandler.teleports.setSecond(teleports);
        PingSpoofHandler.players.setSecond(players);
        PingSpoofHandler.blink.setSecond(blink);
        PingSpoofHandler.movement.setSecond(movement);
        PingSpoofHandler.amount = amount;
    }

    public static void spoof(int amount, boolean regular, boolean velocity, boolean teleports, boolean players, boolean blink) {
        spoof(amount, regular, velocity, teleports, players, blink, false);
    }

    public static void blink() {
        spoof(9999999, true, false, false, false, true);
    }
}