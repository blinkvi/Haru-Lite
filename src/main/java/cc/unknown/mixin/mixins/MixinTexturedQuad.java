package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

@Mixin(TexturedQuad.class)
public class MixinTexturedQuad {
	
    @Unique
    private boolean drawOnSelf;
 
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void patcher$endDraw(Tessellator tessellator) {
    	
    	boolean batchRendering = true;
    	
        if (this.drawOnSelf || !batchRendering) {
            tessellator.draw();
        }
    }
}