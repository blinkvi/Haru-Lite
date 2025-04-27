package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.fml.client.SplashProgress;

@Mixin(value = SplashProgress.class, remap = false)
abstract class MixinSplashProgress {
	@Inject(method = "Lnet/minecraftforge/fml/client/SplashProgress;disableSplash()Z", at = @At("HEAD"), cancellable = true)
	private static void preventSplashScreenAutoDisable(CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue(false);
	}
}