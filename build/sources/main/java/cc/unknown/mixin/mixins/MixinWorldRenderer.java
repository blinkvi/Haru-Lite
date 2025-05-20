package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.IWorldRenderer;
import net.minecraft.client.renderer.WorldRenderer;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {
	
	@Shadow
	private boolean isDrawing;

	@Override
	public boolean isDrawing() {
		return isDrawing;
	}
}
