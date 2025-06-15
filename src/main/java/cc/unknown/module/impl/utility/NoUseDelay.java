package cc.unknown.module.impl.utility;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Slider;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoUseDelay", description = "Remove consume delay.", category = Category.UTILITY)
public class NoUseDelay extends Module {
	
	private final Slider ticks = new Slider("Ticks", this, 17, 1, 30);
	
    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (!isInGame()) return;
        
        if (mc.thePlayer.isUsingItem() && InventoryUtil.getItem() instanceof ItemFood || InventoryUtil.getItem() instanceof ItemPotion || InventoryUtil.getItem() instanceof ItemBucketMilk) {
            int useDuration = mc.thePlayer.getItemInUseDuration();
            if (useDuration >= ticks.getValue()) {
                mc.thePlayer.stopUsingItem();
            }
        }
    }
}