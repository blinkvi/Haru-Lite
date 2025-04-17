package cc.unknown.util.render.shader;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.GLUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.client.gui.ScaledResolution;

public class RoundedUtil implements Accessor {
    public static ShaderUtil roundedShader = new ShaderUtil("roundedRect");
    public static ShaderUtil roundedOutlineShader = new ShaderUtil("roundRectOutline");

    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }
    
    public static void drawRound(double x, double y, double width, double height, double radius, Color color) {
        drawRound((float) x, (float) y, (float) width, (float) height, (float) radius, false, color);
    }

    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
    	ColorUtil.resetColor();
        GLUtil.startBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderUtil.setAlphaLimit(0);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        //roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtil.drawQuad(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GLUtil.endBlend();
    }

    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
    	ColorUtil.resetColor();
        GLUtil.startBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderUtil.setAlphaLimit(0);
        roundedOutlineShader.init();

        ScaledResolution sr = new ScaledResolution(mc);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);


        ShaderUtil.drawQuad(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GLUtil.endBlend();
    }

    public static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtil roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(mc);
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(), (mc.displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }
}
