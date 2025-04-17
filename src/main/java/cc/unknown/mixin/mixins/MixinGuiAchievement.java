package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.achievement.GuiAchievement;

@Mixin(GuiAchievement.class)
public class MixinGuiAchievement {

    @Inject(method = "displayAchievement", at = @At("HEAD"), cancellable = true)
    private void injectAchievements(CallbackInfo ci) {
    	ci.cancel();
    }

    @Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
    private void injectAchievementWindows(CallbackInfo ci) {
    	ci.cancel();
    }
}