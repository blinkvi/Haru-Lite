package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiOptions.class)
public class MixinGuiOptions extends GuiScreen {
	
    @Override
    public void onGuiClosed() {
        mc.gameSettings.saveOptions();
    }
}

