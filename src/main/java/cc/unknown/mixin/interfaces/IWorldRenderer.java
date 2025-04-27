package cc.unknown.mixin.interfaces;

import net.minecraft.client.renderer.WorldRenderer;

public interface IWorldRenderer {
	WorldRenderer pos(double x, double y);
	WorldRenderer color(int color);
	boolean isDrawing();
}
