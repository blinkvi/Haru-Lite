package cc.unknown.mixin.impl;

import net.minecraft.client.renderer.WorldRenderer;

public interface IWorldRenderer {
	WorldRenderer pos(double x, double y);
	WorldRenderer color(int color);
}
