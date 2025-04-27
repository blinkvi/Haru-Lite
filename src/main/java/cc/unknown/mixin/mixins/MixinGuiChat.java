package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.ChatGUIEvent;
import cc.unknown.util.Accessor;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

@Mixin(GuiChat.class)
public class MixinGuiChat {

	@Inject(method = "drawScreen", at = @At("RETURN"))
	private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		Haru.eventBus.handle(new ChatGUIEvent(new ScaledResolution(Accessor.mc), mouseX, mouseY));
	}
}
