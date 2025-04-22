package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.IWorldRenderer;
import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.client.renderer.WorldRenderer;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {

	@Shadow
	public abstract WorldRenderer pos(double x, double y, double z);
	
	@Shadow
	public abstract WorldRenderer color(int red, int green, int blue, int alpha);
	
	@Override
	public WorldRenderer pos(double x, double y) {
		 return this.pos(x, y, 0.0);
	}

	@Override
	public WorldRenderer color(int color) {
        return this.color(ColorUtil.getRedFromColor(color), ColorUtil.getGreenFromColor(color), ColorUtil.getBlueFromColor(color), ColorUtil.getAlphaFromColor(color));
	}
}
