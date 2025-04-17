package cc.unknown.module.impl.utility;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoSlow;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoUseDelay", description = "Speeds up the player's item usage, such as eating food or drinking potions.", category = Category.UTILITY)
public class NoUseDelay extends Module {
	
	private final SliderValue ticks = new SliderValue("Ticks", this, 28, 1, 32);
	
	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (mc.thePlayer.isUsingItem() && InventoryUtil.getItemStack().getItem() instanceof ItemFood) {
			if (isEnabled(NoSlow.class, NoItemRelease.class)) return;
			
            int foodUseDuration = mc.thePlayer.getItemInUseDuration();
            int halfDuration = (int) ticks.getValue();
            if (foodUseDuration >= halfDuration) {
            	mc.thePlayer.stopUsingItem();
            }
		}
	}
	
}