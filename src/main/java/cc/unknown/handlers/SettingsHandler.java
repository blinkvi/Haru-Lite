package cc.unknown.handlers;
import cc.unknown.event.player.PreAttackEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.impl.move.NoSlow;
import cc.unknown.module.impl.utility.NoItemRelease;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.player.InventoryUtil;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SettingsHandler implements Accessor {

    @SubscribeEvent
    public void onPreAttack(PreAttackEvent event) {
        if (!isInGame()) return;

        getDropGui().getWindows().stream().forEach(window -> {
            if (window.getSettingBools().get(0).get()) {
                ReflectUtil.setLeftClickCounter(0);
            }

            if (window.getSettingBools().get(1).get()) {
                ReflectUtil.setJumpTicks(0);
            }
        });
    }

    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (!isInGame()) return;

        getDropGui().getWindows().stream()
            .filter(window -> window.getSettingBools().get(2).get() && mc.thePlayer.isUsingItem() && InventoryUtil.getItemStack().getItem() instanceof ItemFood)
            .findFirst()
            .ifPresent(window -> {
                if (getModule(NoSlow.class).isEnabled() && getModule(NoItemRelease.class).isEnabled()) return;

                int foodUseDuration = mc.thePlayer.getItemInUseDuration();
                if (foodUseDuration >= 26) {
                    mc.thePlayer.stopUsingItem();
                }
            });
    }
}
