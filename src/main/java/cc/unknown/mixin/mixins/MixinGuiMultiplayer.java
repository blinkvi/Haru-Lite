package cc.unknown.mixin.mixins;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.ui.menu.AltManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen {
	
	@Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
	private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
		if (keyCode == Keyboard.KEY_LSHIFT) {
			mc.displayGuiScreen(new AltManager());
		}
	}

	@Redirect(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
	private void redirectToMainMenu(Minecraft mc, GuiScreen screen) {
		mc.displayGuiScreen(new GuiMainMenu());
	}
}
