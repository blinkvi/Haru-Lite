package cc.unknown.module.impl.combat;

import cc.unknown.event.player.PreTickEvent;
import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "RightClicker", description = "Automatically right-clicks for you.", category = Category.COMBAT)
public class RightClicker extends Module {

    private final Slider minCps = new Slider("MinCPS", this, 10, 1, 60);
    private final Slider maxCps = new Slider("MaxCPS", this, 10, 1, 60);

    private final Clock clock = new Clock();

    @Override
    public void onEnable() {
        if (!isInGame()) return;
        clock.reset();
    }

    @Override
    public void guiUpdate() {
        correct(minCps, maxCps);
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (!isInGame()) return;
        ReflectUtil.setRightClickCounter(0);
    }

    @SubscribeEvent
    public void onTick(PreTickEvent event) {
        if (!isInGame()) return;

        try {

            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                if (clock.hasPassed(1000 / MathUtil.nextInt(minCps.getAsInt(), maxCps.getAsInt()))) {
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                    PlayerUtil.setMouseButtonState(0, true);
                    clock.reset();
                }
            }
        } catch (NullPointerException e) {

        }
    }
}
