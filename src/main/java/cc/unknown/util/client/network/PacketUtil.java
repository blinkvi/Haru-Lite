package cc.unknown.util.client.network;

import cc.unknown.util.Accessor;
import net.minecraft.network.Packet;

@SuppressWarnings("rawtypes")
public class PacketUtil implements Accessor {

	public static void send(final Packet packet) {
		mc.getNetHandler().addToSendQueue(packet);
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
}