package cc.unknown.module.impl.utility;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.Slider;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "FastUse", description = "Remove consume delay.", category = Category.UTILITY)
public class FastUse extends Module {
	
	private final Mode mode = new Mode("Mode", this, "Reset", "Reset");
	private final Slider ticks = new Slider("Ticks", this, 17, 1, 30, () -> mode.is("Reset"));
	
    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (!isInGame()) return;
        
        switch (mode.getMode()) {
        case "Reset":
            if (mc.thePlayer.isUsingItem() && InventoryUtil.getItem() instanceof ItemFood || InventoryUtil.getItem() instanceof ItemPotion || InventoryUtil.getItem() instanceof ItemBucketMilk) {
                int useDuration = mc.thePlayer.getItemInUseDuration();
                if (useDuration >= ticks.getValue()) {
                    mc.thePlayer.stopUsingItem();
                }
            }
        	break;
        }
    }
}