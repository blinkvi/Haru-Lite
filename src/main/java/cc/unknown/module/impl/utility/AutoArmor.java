package cc.unknown.module.impl.utility;

import cc.unknown.event.player.PreMoveInputEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoArmor", description = "", category = Category.UTILITY)
public class AutoArmor extends Module {
	private final Slider startDelay = new Slider("StartDelay", this, 1, 0, 10, 1);
	private final Slider speed = new Slider("Speed", this, 1, 0, 10, 1);

	private final Clock startTimer = new Clock();
	private final Clock clock = new Clock();

	@SubscribeEvent
	public void onMoveInput(PreMoveInputEvent event) {
		if (mc.currentScreen == null) {
			startTimer.reset();
		}

		if (!startTimer.hasElapsedTicks((int) startDelay.getValue(), false)) {
			return;
		}

		if (clock.hasElapsedTicks(speed.getAsInt(), false)) {
			if (!(mc.currentScreen instanceof GuiInventory)) {
				return;
			}

			for (int type = 1; type < 5; ++type) {
				if (clock.hasElapsedTicks(speed.getAsInt(), false)) {
					if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
						ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
						if (!InventoryUtil.bestArmor(is, type)) {
							PacketUtil.windowsClick(4 + type, "DropItem");

							clock.reset();
							if (speed.getValue() != 0) {
								break;
							}
						}
					}
				}
			}
			
			for (int type = 1; type < 5; ++type) {
				if (clock.hasElapsedTicks(speed.getAsInt(), false)) {
					for (int i = 9; i < 45; ++i) {
						if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
							ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
							if (validArmor(is, type)) {
								PacketUtil.windowsClick(i, "Shift");

								clock.reset();
								if (speed.getValue() != 0) {
									break;
								}
							}
						}
					}
				}
			}
		}

	}

	private boolean validArmor(ItemStack itemStack, int type) {
		return InventoryUtil.armorProtection(itemStack) > 0.0F && InventoryUtil.bestArmor(itemStack, type) && !InventoryUtil.trash(itemStack, true, true);
	}
}