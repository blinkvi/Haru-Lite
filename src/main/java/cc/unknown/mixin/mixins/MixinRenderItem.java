package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.util.player.EnumFacings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

@Mixin(RenderItem.class)
public class MixinRenderItem {
	@Inject(method = "renderItemAndEffectIntoGUI", at = @At("HEAD"))
	public void preRenderItemAndEffectIntoGUIhead(final ItemStack stack, int xPosition, int yPosition, CallbackInfo ci) {
        GlStateManager.enableDepth();
	}
	
	@Inject(method = "renderItemOverlayIntoGUI", at = @At("HEAD"))
	public void preRenderItemOverlayIntoGUIhead(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
        GlStateManager.enableDepth();
	}
	
    @Redirect(method = "renderModel(Lnet/minecraft/client/resources/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    private EnumFacing[] renderModel$getCachedArray() {
        return EnumFacings.FACINGS;
    }
}
