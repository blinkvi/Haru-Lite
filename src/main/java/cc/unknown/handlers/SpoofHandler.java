package cc.unknown.handlers;

import cc.unknown.util.Accessor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpoofHandler implements Accessor  {
	private static int spoofedSlot;

	private static boolean spoofing;

	public static void startSpoofing(int slot) {
		spoofing = true;
		spoofedSlot = slot;
	}

	public static void stopSpoofing() {
		spoofing = false;
	}

	public static int getSpoofedSlot() {
		return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
	}

	public static ItemStack getSpoofedStack() {
		return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
	}

	@SubscribeEvent
	public void onWorld(WorldEvent.Load event) {
		stopSpoofing();
	}

	public static boolean isSpoofing() {
		return spoofing;
	}
}