package cc.unknown.handlers;
import java.util.List;

import cc.unknown.event.player.PreAttackEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.BoolValue;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SettingsHandler implements Accessor {
	
    @SubscribeEvent
    public void onPreAttack(PreAttackEvent event) {
        if (!isInGame()) return;

        getDropGui().getWindows().stream().forEach(window -> {
            if (getName(window.getSettingBools(), "NoHitDelay")) {
                ReflectUtil.setLeftClickCounter(0);
            }

            if (getName(window.getSettingBools(), "NoJumpDelay")) {
                ReflectUtil.setJumpTicks(0);
            }
        });
    }

    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (!isInGame()) return;
        try {
            getDropGui().getWindows().stream()
            .filter(window -> getName(window.getSettingBools(), "NoUseDelay") && mc.thePlayer.isUsingItem() && InventoryUtil.getItemStack().getItem() instanceof ItemFood || InventoryUtil.getItemStack().getItem() instanceof ItemPotion || InventoryUtil.getItemStack().getItem() instanceof ItemBucketMilk)
            .findFirst()
            .ifPresent(window -> {
                int useDuration = mc.thePlayer.getItemInUseDuration();
                if (useDuration >= 20) {
                    mc.thePlayer.stopUsingItem();
                }
            });	
        } catch (Exception menta) {
        	
        }
    }
	
    private boolean getName(List<BoolValue> value, String name) {
        return value.stream().filter(setting -> setting.getName().equalsIgnoreCase(name)).map(BoolValue::get).findFirst().orElse(false);
    }
}
