package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.event.render.ChatGUIEvent;
import cc.unknown.util.Accessor;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;

@Mixin(GuiChat.class)
public class MixinGuiChat {

	@Inject(method = "drawScreen", at = @At("RETURN"))
	private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		 MinecraftForge.EVENT_BUS.post(new ChatGUIEvent(new ScaledResolution(Accessor.mc), mouseX, mouseY));
	}
}
