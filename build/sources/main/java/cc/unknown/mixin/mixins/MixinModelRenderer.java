package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer {

	@Shadow
	private boolean compiled;
	
	private boolean compiledState;
	
    @Inject(method = "render", at = @At("HEAD"))
    private void resetCompiled(float j, CallbackInfo ci) {
    	
    	boolean batchRendering = true;
    	
        if (compiledState != batchRendering) {
            this.compiled = false;
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/Tessellator;getWorldRenderer()Lnet/minecraft/client/renderer/WorldRenderer;"))
    private void beginRendering(CallbackInfo ci) {
    	
    	boolean batchRendering = true;    	
        this.compiledState = batchRendering;
        
        if (batchRendering) {
            Tessellator.getInstance().getWorldRenderer().begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEndList()V", remap = false))
    private void draw(CallbackInfo ci) {
    	
    	boolean batchRendering = true;
    	
        if (batchRendering) {
            Tessellator.getInstance().draw();
        }
    }
}