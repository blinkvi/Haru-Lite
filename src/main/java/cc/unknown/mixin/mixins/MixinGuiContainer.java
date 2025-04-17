package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {

    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);
    
    @Shadow 
    private int dragSplittingButton;
    
    @Shadow 
    private int dragSplittingRemnant;
    
    @Inject(method = "updateDragSplitting", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void fixRemnants(CallbackInfo ci) {
        if (this.dragSplittingButton == 2) {
            this.dragSplittingRemnant = mc.thePlayer.inventory.getItemStack().getMaxStackSize();
            ci.cancel();
        }
    }
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton - 100 == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.thePlayer.closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void postMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        checkHotbarKeys(mouseButton - 100);
    }
    
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawDefaultBackground()V"))
    public void removeDrawDefaultBackground() {}
}