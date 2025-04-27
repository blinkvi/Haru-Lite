package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.Haru;
import cc.unknown.event.impl.RenderTextEvent;
import net.minecraft.client.gui.FontRenderer;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

	@Shadow
	protected abstract void resetStyles();

	@ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0)
	private String renderString(String text) {
		if (text == null) {
			return text;
		}

		RenderTextEvent event = new RenderTextEvent(text);
		Haru.eventBus.handle(event);
		return event.string;
	}

	@ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0)
	private String getStringWidth(String text) {
		if (text == null) {
			return text;
		}

		RenderTextEvent event = new RenderTextEvent(text);
		Haru.eventBus.handle(event);
		return event.string;
	}

	@Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I", ordinal = 0, shift = At.Shift.AFTER))
	private void resetStyle(CallbackInfoReturnable<Integer> ci) {
		this.resetStyles();
	}
}
