package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {
    @Shadow
    protected abstract boolean bindEntityTexture(T entity);
    
    @Shadow
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
    	
    }
}