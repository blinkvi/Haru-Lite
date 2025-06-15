package cc.unknown.module.impl.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ChestStealer", description = "", category = Category.UTILITY)
public class ChestStealer extends Module {

	private final Bool mouseControl = new Bool("MouseControl", this, false);
	private final Bool notMoving = new Bool("NotMoving", this, false);
	private final Slider minStartDelay = new Slider("MinStartDelay", this, 100, 0, 500, 10);
	private final Slider maxStartDelay = new Slider("MaxStartDelay", this, 200, 0, 500, 10);
	private final Slider minStealDelay = new Slider("MinStealDelay", this, 100, 0, 500, 10);
	private final Slider maxStealDelay = new Slider("MaxStartDelay", this, 150, 0, 500, 10);

	private final Bool shuffle = new Bool("Shuffle", this, false);
	private final Bool autoClose = new Bool("AutoClose", this, false);
	private final Bool autoCloseIfInvFull = new Bool("AutoCloseIfInvFull", this, true, autoClose::get);
	private final Slider minCloseDelay = new Slider("MinCloseDelay", this, 50, 0, 500, 10, autoClose::get);
	private final Slider maxCloseDelay = new Slider("MaxCloseDelay", this, 100, 0, 500, 10, autoClose::get);
	private final Bool ignoreTrash = new Bool("Ignore trash", false);

	private static State state = State.NONE;
	private final Set<Integer> stole = new HashSet<>();
	private long nextStealTime;
	private long nextCloseTime;

	@Override
	public void onEnable() {
		stole.clear();
	}

	@Override
	public void guiUpdate() {
		correct(minStartDelay, maxStartDelay);
		correct(minStealDelay, maxStealDelay);
		correct(minCloseDelay, maxCloseDelay);
	}

	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (mc.currentScreen instanceof GuiChest) {
			if (notMoving.get()) {
				mc.thePlayer.motionX = 0;
				mc.thePlayer.motionZ = 0;
			}

			if (mouseControl.get()) {
				mc.inGameHasFocus = true;
				mc.mouseHelper.grabMouseCursor();
			}

			switch (state) {
			case STEAL:
				while (nextStealTime <= System.currentTimeMillis()) {
					if (autoCloseIfInvFull.get() && InventoryUtil.isFull()) {
						close();
						return;
					}
					final ContainerChest containerChest = (ContainerChest) mc.thePlayer.openContainer;

					final List<Integer> items = getUnStoleItems(containerChest);
					if (items.isEmpty()) {
						close();
						return;
					}

					final int slot = items.get(0);

					stole.add(slot);
					PacketUtil.windowsClick(slot, "Shift");
					nextStealTime = System.currentTimeMillis()
							+ MathUtil.randomizeInt(minStealDelay.getAsInt(), maxStealDelay.getAsInt());
				}
				break;
			case AFTER:
				if (autoClose.get() && nextCloseTime <= System.currentTimeMillis()) {
					mc.thePlayer.closeScreen();
					state = State.NONE;
				}
				break;
			default:
				break;
			}
		}
	}

	private void close() {
		nextCloseTime = MathUtil.randomizeInt(minCloseDelay.getAsInt(), maxCloseDelay.getAsInt());
		state = State.AFTER;
	}

	private List<Integer> getUnStoleItems(ContainerChest containerChest) {
		IInventory chest = containerChest.getLowerChestInventory();
		List<Integer> items = new ArrayList<>(chest.getSizeInventory());
		for (int i = 0; i < chest.getSizeInventory(); i++) {
			if (stole.contains(i))
				continue;
			ItemStack stack = chest.getStackInSlot(i);
			if (stack == null || stack.getItem() instanceof ItemEmptyMap) continue;
			if (ignoreTrash.get() && InventoryUtil.trash(stack, true, true)) continue;
			items.add(i);
		}

		if (shuffle.get()) {
			Collections.shuffle(items);
		}

		return items;
	}

	enum State {
		NONE, BEFORE, STEAL, AFTER
	}
}