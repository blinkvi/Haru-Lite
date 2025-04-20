package cc.unknown.mixin.mixins;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

@Mixin(value = EventBus.class, remap = false)
public abstract class MixinEventBus {
	@Inject(method = "post", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Throwables;propagate(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;"), cancellable = true)
	public void onPostHandleException(Event event, @NotNull CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}
}