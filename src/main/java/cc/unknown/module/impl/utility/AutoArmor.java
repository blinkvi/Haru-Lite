package cc.unknown.module.impl.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoArmor", description = "", category = Category.UTILITY)
public class AutoArmor extends Module {
	private final SliderValue delay = new SliderValue("Delay", this, 150, 0, 300, 25);
	private final BoolValue openInv = new BoolValue("Open Inventory", true);
	private final BoolValue noMove = new BoolValue("Disable Movement", true);

	private final Clock clock = new Clock(0L);
	private boolean movedItem;

	@Override
	public void onEnable() {
		clock.reset();
	}

	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
	    if (!clock.reached(delay.getValue()) || mc.currentScreen instanceof GuiChest || (MoveUtil.isMoving() && noMove.get())) {
	        mc.thePlayer.closeScreen();
	        return;
	    }

	    if (!(mc.currentScreen instanceof GuiInventory) && openInv.get()) return;

	    movedItem = false;
	    clock.reset();

	    Map<Integer, Integer> bestArmor = new HashMap<>();

	    IntStream.range(0, 40).forEach(i -> {
	        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
	        if (stack != null && stack.getItem() instanceof ItemArmor) {
	            ItemArmor armor = (ItemArmor) stack.getItem();
	            int type = armor.armorType;
	            int reduction = getArmorDamageReduction(stack);

	            bestArmor.compute(type, (k, v) -> (v == null || reduction > getArmorDamageReduction(mc.thePlayer.inventory.getStackInSlot(v))) ? i : v);
	        }
	    });

	    bestArmor.forEach((type, slotIndex) -> equipArmor(getSlotId(slotIndex)));
	}

	private int getArmorDamageReduction(ItemStack stack) {
	    if (stack == null || !(stack.getItem() instanceof ItemArmor)) return 0;
	    ItemArmor armor = (ItemArmor) stack.getItem();
	    int baseReduction = armor.damageReduceAmount;
	    int enchantmentReduction = EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{stack}, DamageSource.generic);

	    return baseReduction + enchantmentReduction;
	}
	
	private void equipArmor(int slot) {
	    if (slot >= 0 && slot < mc.thePlayer.inventoryContainer.inventorySlots.size() && slot > 8 && !movedItem) {
	        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
	        movedItem = true;
	    }
	}

	private int getSlotId(int slot) {
	    return (slot < 9) ? slot + 36 : slot;
	}
}