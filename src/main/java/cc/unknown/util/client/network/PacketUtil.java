package cc.unknown.util.client.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.client.ChatUtil;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;

public class PacketUtil implements Accessor {

    public static List<Packet<INetHandlerPlayServer>> skipSendEvent = new ArrayList<>();
    public static List<Packet<INetHandlerPlayClient>> skipReceiveEvent = new ArrayList<>();

    public static void sendNoEvent(Packet<?> packet) {
        if (packet == null)
            return;
        try {
            Packet<INetHandlerPlayServer> casted = castPacket(packet);
            skipSendEvent.add(casted);
            mc.thePlayer.sendQueue.addToSendQueue(casted);
        } catch (ThreadQuickExitException | ClassCastException ignored) {
        }
    }

    public static void send(Packet<?> packet) {
        if (packet == null)
            return;
        try {
            Packet<INetHandlerPlayServer> casted = castPacket(packet);
            mc.thePlayer.sendQueue.addToSendQueue(casted);
        } catch (ThreadQuickExitException | ClassCastException ignored) {
        }
    }

    public static void receiveNoEvent(Packet<?> packet) {
        if (packet == null)
            return;
        try {
            Packet<INetHandlerPlayClient> casted = castPacket(packet);
            skipReceiveEvent.add(casted);
            casted.processPacket(mc.getNetHandler());
        } catch (ThreadQuickExitException ignored) {
        } catch (Exception e) {
            onBadPacket(packet, e);
        }
    }

    public static void receive(Packet<?> packet) {
        if (packet == null)
            return;
        try {
            Packet<INetHandlerPlayClient> casted = castPacket(packet);
            casted.processPacket(mc.getNetHandler());
        } catch (ThreadQuickExitException ignored) {
        } catch (Exception e) {
            onBadPacket(packet, e);
        }
    }
    
    public static void onBadPacket(Packet<?> packet, Exception e) {
        try {
            final StringBuilder stackTraces = new StringBuilder();

            Arrays.stream(e.getStackTrace()).limit(7).parallel().map(s -> "\n  " + ChatFormatting.RED + "at " + ChatFormatting.AQUA + s).forEach(stackTraces::append);
            ChatUtil.chat(String.format(
                    "%sCatch %s on processing packet <%s>.%s",
                    ChatFormatting.RED, e.getClass(), packet, stackTraces
            ));
        } catch (Throwable ignored) {
        }
    }
    
    public static void queue(final Packet packet) {
        if (packet == null) {
            System.out.println("Packet is null");
            return;
        }

        if (isClientPacket(packet)) {
        	sendNoEvent(packet);
        } else {
            packet.processPacket(mc.getNetHandler().getNetworkManager().getNetHandler());
        }
    }
    
    public static boolean isClientPacket(final Packet<?> packet) {
        return Arrays.stream(NetworkAPI.serverbound).anyMatch(clazz -> clazz == packet.getClass());
    }
	
	public static void windowsClick(int slot, String name) {
		switch (name) {
		case "Shift":
			mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
			break;
		case "DropItem":
			mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
			break;
		}
	}

    @SuppressWarnings("unchecked")
    public static <H extends INetHandler> Packet<H> castPacket(Packet<?> packet) throws ClassCastException {
        return (Packet<H>) packet;
    }
}