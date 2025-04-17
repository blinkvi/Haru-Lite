package cc.unknown.util.render;

import static cc.unknown.util.render.RenderUtil.drawArc;
import static cc.unknown.util.render.RenderUtil.drawCircle;
import static cc.unknown.util.render.RenderUtil.drawCircleWithOffsets;
import static cc.unknown.util.render.RenderUtil.drawDecorativeCircle;
import static cc.unknown.util.render.RenderUtil.drawHaloRectangles;
import static cc.unknown.util.render.RenderUtil.drawLineExtensions;
import static cc.unknown.util.render.RenderUtil.drawStar;
import static cc.unknown.util.render.RenderUtil.getExtraWidth;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class HaloRenderer implements Accessor {
	
	public static void drawHalo(String mode) {
		float height = mc.thePlayer.height + 0.25f;
		float yaw = mc.thePlayer.rotationYaw;
		float partialTicks = ReflectUtil.getTimer().renderPartialTicks;
        float extraHeight = 0.035f;
        float extensionLength = 0.18f;
        float smallExtensionLength = 0.08f;
		
		switch (mode) {
		case "Aris":
		    GL11.glPushMatrix();
		    glTranslated(partialTicks, height);
		    setupOpenGL();
		    GL11.glRotatef(-yaw, 0F, 1F, 0F);
		    GL11.glRotatef(90, 1F, 0F, 0F);

		    drawHaloRectangles();

		    resetOpenGL();
		    GL11.glPopMatrix();
			break;
		case "Shiroko":
	        GL11.glPushMatrix();
	        glTranslated(partialTicks, height);
	        setupOpenGL();

	        ColorUtil.color(new Color(MathHelper.clamp_int(2000 * 1800, 0, 255), MathHelper.clamp_int(230 + 2000 * 200, 0, 255), 250, 220));

	        GL11.glRotatef(-yaw, 0F, 1F, 0F);
	        GL11.glRotatef(90, 1F, 0F, 0F);

	        drawCircle(0.18f, 2.5f);

	        GL11.glTranslated(0.0f, 0.0f, -0.02f);
	        drawDecorativeCircle(0.3f, 3.7f);

	        resetOpenGL();
	        GL11.glPopMatrix();
			break;
		case "Reisa":
	        GL11.glPushMatrix();
	        glTranslated(partialTicks, height);
	        setupOpenGL();

	        GL11.glRotatef(-yaw, 0F, 1F, 0F);
	        GL11.glRotatef(90, 1F, 0F, 0F);

	        ColorUtil.color(new Color(200, 200, 250, 220));
	        GL11.glLineWidth(3.0f * getExtraWidth());
	        drawStar(0.0f, 0.0f, 0.3f, 0);

	        GL11.glPushMatrix();
	        GL11.glRotatef(36, 0F, 0F, 1F);
	        drawStar(0.0f, 0.0f, 0.14f, 0);
	        GL11.glPopMatrix();

	        resetOpenGL();
	        GL11.glPopMatrix();
			break;
		case "Natsu":
	        GL11.glPushMatrix();
	        glTranslated(partialTicks, height);
	        setupOpenGL();

	        GL11.glRotatef(-yaw, 0F, 1F, 0F);
	        GL11.glRotatef(90, 1F, 0F, 0F);

	        ColorUtil.color(new Color(254, 200, 200, 240));

	        drawCircle(0.3f, 3.5f);
	        drawCircleWithOffsets(0.15f, 0.05f, 0.05f, 3.5f);

	        resetOpenGL();
	        GL11.glPopMatrix();
			break;
		case "Hoshino":
	        GL11.glPushMatrix();
	        glTranslated(partialTicks, height);
	        setupOpenGL();
	        GL11.glRotatef(-yaw, 0F, 1F, 0F);
	        GL11.glRotatef(90, 1F, 0F, 0F);

	        drawCircle(0.13f, 4.0f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));
	        GL11.glTranslated(0.0f, 0.0f, -extraHeight);
	        drawCircle(0.20f, 2.5f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));
	        GL11.glTranslated(0.0f, 0.0f, -extraHeight);

	        drawArc(0.27f, 15, 165, 4.0f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));
	        drawArc(0.27f, 195, 345, 4.0f, new Color(237, MathHelper.clamp_int(110 + 2000 * 600, 0, 255), 183, 220));

	        drawLineExtensions(0.27f, extensionLength, new int[]{0, 180});

	        drawLineExtensions(0.27f, smallExtensionLength, new int[]{15, 165, 195, 345});

	        resetOpenGL();
	        GL11.glPopMatrix();
			break;
		}	
	}
	
    public static void setupOpenGL() {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void resetOpenGL() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
    }
    
    public static void glTranslated(float partialTicks, float height) {
        GL11.glTranslated(mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks - ReflectUtil.getRenderPosX(), mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks - ReflectUtil.getRenderPosY() + height, mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks - ReflectUtil.getRenderPosZ());
    }
}