package cc.unknown.util.render.shader.impl;

import java.awt.image.BufferedImage;

import cc.unknown.util.render.shader.Shader;

public class ShaderScissor extends Shader {
    private final int x, y, width, height;
    private final BufferedImage in;

    public ShaderScissor(int x, int y, int width, int height, BufferedImage in, int level, boolean antiAlias, boolean multithreading) {
        super(level, antiAlias, multithreading);
        this.x = x * level;
        this.y = y * level;
        this.width = width * level;
        this.height = height * level;
        this.in = in;
        setWidth(in.getWidth());
        setHeight(in.getHeight());
    }

    @Override
    public int dispose(int relativeX, int relativeY, float screenWidth, float screenHeight) {
        return (relativeX > x && relativeY > y && relativeX < x + width && relativeY < y + height) ? 0 : in.getRGB(relativeX / level, relativeY / level);
    }

    @Override
    public Object[] params() {
        return new Object[]{in, x, y, width, height, level, antiAlias};
    }
}