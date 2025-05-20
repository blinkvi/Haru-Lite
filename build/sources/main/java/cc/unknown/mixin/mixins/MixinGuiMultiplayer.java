package cc.unknown.mixin.mixins;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.ui.menu.AltManager;
import net.minecraft.client.gui.GuiButton;
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
	
    @Inject(method = "actionPerformed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiButton;id:I", args = "ldc=0"), cancellable = true)
    protected void onActionPerformed(GuiButton button, CallbackInfo ci) throws IOException {
        if (button.id == 0) {
        	this.mc.displayGuiScreen(new GuiMainMenu());
            ci.cancel();
        }
    }
}
