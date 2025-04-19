package cc.unknown.util.render;

import static java.lang.Math.PI;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertex3i;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.render.shader.filters.GaussianFilter;
import cc.unknown.util.render.shader.impl.ShaderScissor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public final class RenderUtil implements Accessor {

	private final Map<Integer, Integer> shadowCache = new HashMap<>();
	private final Map<EntityPlayer, float[][]> rotationMap = new HashMap<>();
	private final float DEGREES_IN_RADIAN = 57.295776f;

	public void drawRect(double left, double top, double right, double bottom, int color) {
		double j;
		if (left < right) {
			j = left;
			left = right;
			right = j;
		}

		if (top < bottom) {
			j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0).endVertex();
		worldrenderer.pos(right, bottom, 0.0).endVertex();
		worldrenderer.pos(right, top, 0.0).endVertex();
		worldrenderer.pos(left, top, 0.0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public void drawRect(float left, float top, float width, float height, Color color) {
		drawRect(left, top, width, height, color.getRGB());
	}

	public void drawRect(float left, float top, float width, float height, int color) {
		float right = left + width, bottom = top + height;
		if (left < right) {
			float i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			float j = top;
			top = bottom;
			bottom = j;
		}

		Gui.drawRect((int) left, (int) top, (int) right, (int) bottom, color);
	}

	public void renderItemStack(ItemStack stack, double x, double y, float scale) {
		renderItemStack(stack, x, y, scale, false);
	}

	public void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText) {
		renderItemStack(stack, x, y, scale, enchantedText, scale);
	}

	public void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText,
			float textScale) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, x);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
		mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, 0, 0);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public void renderItemStack(EntityPlayer target, float x, float y, float scale, boolean enchantedText,
			float textScale, boolean bg, boolean info) {
		List<ItemStack> items = new ArrayList<>();
		if (target.getHeldItem() != null) {
			items.add(target.getHeldItem());
		}
		for (int index = 3; index >= 0; index--) {
			ItemStack stack = target.inventory.armorInventory[index];
			if (stack != null) {
				items.add(stack);
			}
		}
		float i = x;

		for (ItemStack stack : items) {
			if (bg)
				drawRect(i, y, 16 * scale, 16 * scale, new Color(0, 0, 0, 150).getRGB());
			renderItemStack(stack, i, y, scale, enchantedText, textScale);
			i += 16;
		}
	}

	public void renderItemStack(EntityPlayer target, float x, float y, float scale, boolean bg, boolean info) {
		renderItemStack(target, x, y, scale, false, 0, bg, info);
	}

	public void renderItemStack(EntityPlayer target, float x, float y, float scale, float textScale) {
		renderItemStack(target, x, y, scale, true, textScale, false, false);
	}

	public void renderItemStack(EntityPlayer target, float x, float y, float scale) {
		renderItemStack(target, x, y, scale, scale);
	}
	
	public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		ColorUtil.color(Color.WHITE);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		mc.getTextureManager().bindTexture(imageLocation);
		Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, (int) 0, (int) 0, (int) width, (int) height, width, height);
		GlStateManager.resetColor();
		GlStateManager.disableBlend();
	}

	public void image(final ResourceLocation imageLocation, final int x, final int y, final int width, final int height) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		ColorUtil.color(Color.WHITE);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		mc.getTextureManager().bindTexture(imageLocation);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		GlStateManager.resetColor();
		GlStateManager.disableBlend();
	}

	public void image(final File imageFile, final int x, final int y, final int width, final int height) {
		if (!imageFile.exists())
			return;

		try {
			BufferedImage bufferedImage = ImageIO.read(imageFile);
			DynamicTexture dynamicTexture = new DynamicTexture(bufferedImage);
			int textureId = dynamicTexture.getGlTextureId();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

			Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

			GlStateManager.resetColor();
			GlStateManager.disableBlend();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setAlphaLimit(float limit) {
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
	}

	public void drawRoundedRect(double d, double e, double g, double h, float radius, int color) {
		float x1 = (float) (d + g), // @off
				y1 = (float) (e + h);
		final float f = (color >> 24 & 0xFF) / 255.0F, f1 = (color >> 16 & 0xFF) / 255.0F,
				f2 = (color >> 8 & 0xFF) / 255.0F, f3 = (color & 0xFF) / 255.0F; // @on
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);

		d *= 2;
		e *= 2;
		x1 *= 2;
		y1 *= 2;

		glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(f1, f2, f3, f);
		GlStateManager.enableBlend();
		glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glBegin(GL11.GL_POLYGON);
		final double v = PI / 180;

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(d + radius + MathHelper.sin((float) (i * v)) * (radius * -1),
					e + radius + MathHelper.cos((float) (i * v)) * (radius * -1));
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(d + radius + MathHelper.sin((float) (i * v)) * (radius * -1),
					y1 - radius + MathHelper.cos((float) (i * v)) * (radius * -1));
		}

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius,
					y1 - radius + MathHelper.cos((float) (i * v)) * radius);
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius,
					e + radius + MathHelper.cos((float) (i * v)) * radius);
		}

		GL11.glEnd();

		glEnable(GL11.GL_TEXTURE_2D);
		glDisable(GL11.GL_LINE_SMOOTH);
		glEnable(GL11.GL_TEXTURE_2D);

		GL11.glScaled(2, 2, 2);

		GL11.glPopAttrib();
		GL11.glColor4f(1, 1, 1, 1);
	}

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
    	float zLevel = 0;
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
	
	public void drawGradientRect(final double left, final double top, double right, double bottom,
			final boolean sideways, final int startColor, final int endColor) {
		right = left + right;
		bottom = top + bottom;
		GL11.glDisable(3553);
		GLUtil.startBlend();
		GL11.glShadeModel(7425);
		GL11.glBegin(7);
		ColorUtil.color(startColor);
		if (sideways) {
			GL11.glVertex2d(left, top);
			GL11.glVertex2d(left, bottom);
			ColorUtil.color(endColor);
			GL11.glVertex2d(right, bottom);
			GL11.glVertex2d(right, top);
		} else {
			GL11.glVertex2d(left, top);
			ColorUtil.color(endColor);
			GL11.glVertex2d(left, bottom);
			GL11.glVertex2d(right, bottom);
			ColorUtil.color(startColor);
			GL11.glVertex2d(right, top);
		}
		GL11.glEnd();
		GL11.glDisable(3042);
		GL11.glShadeModel(7424);
		GLUtil.endBlend();
		GL11.glEnable(3553);
	}

	public void drawBorder(float x, float y, float width, float height, final float outlineThickness,
			int outlineColor) {
		glEnable(GL_LINE_SMOOTH);
		ColorUtil.color(outlineColor);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();

		glLineWidth(outlineThickness);
		float cornerValue = (float) (outlineThickness * .19);

		glBegin(GL_LINES);
		glVertex2d(x, y - cornerValue);
		glVertex2d(x, y + height + cornerValue);
		glVertex2d(x + width, y + height + cornerValue);
		glVertex2d(x + width, y - cornerValue);
		glVertex2d(x, y);
		glVertex2d(x + width, y);
		glVertex2d(x, y + height);
		glVertex2d(x + width, y + height);
		glEnd();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();

		glDisable(GL_LINE_SMOOTH);
	}

	public void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness,
			int rectColor, int outlineColor) {
		drawRect(x, y, width, height, rectColor);
		drawBorder(x, y, width, height, outlineThickness, outlineColor);
	}

	public void drawHorizontalGradientSideways(double x, double y, double width, double height, int leftColor,
			int rightColor) {
		drawGradientRect(x, y, width, height, true, leftColor, rightColor);
	}

	public void drawVerticalGradientSideways(double x, double y, double width, double height, int topColor,
			int bottomColor) {
		drawGradientRect(x, y, width, height, false, topColor, bottomColor);
	}

	public void setupRenderState(Color color, float width) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		ColorUtil.glColor(color.getRGB());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
	}

	public void restoreRenderState() {
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void updatePlayerAngles(EntityPlayer entityPlayer, ModelBiped modelBiped) {
		rotationMap.put(entityPlayer,
				new float[][] {
						{ modelBiped.bipedHead.rotateAngleX, modelBiped.bipedHead.rotateAngleY,
								modelBiped.bipedHead.rotateAngleZ },
						{ modelBiped.bipedRightArm.rotateAngleX, modelBiped.bipedRightArm.rotateAngleY,
								modelBiped.bipedRightArm.rotateAngleZ },
						{ modelBiped.bipedLeftArm.rotateAngleX, modelBiped.bipedLeftArm.rotateAngleY,
								modelBiped.bipedLeftArm.rotateAngleZ },
						{ modelBiped.bipedRightLeg.rotateAngleX, modelBiped.bipedRightLeg.rotateAngleY,
								modelBiped.bipedRightLeg.rotateAngleZ },
						{ modelBiped.bipedLeftLeg.rotateAngleX, modelBiped.bipedLeftLeg.rotateAngleY,
								modelBiped.bipedLeftLeg.rotateAngleZ } });
	}

	public void drawSkeleton(EntityPlayer player, float partialTicks) {
		float[][] entPos = rotationMap.get(player);
		if (entPos != null) {
			glPushMatrix();

			float x = (float) (interpolate(player.posX, player.prevPosX, partialTicks) - ReflectUtil.getRenderPosX());
			float y = (float) (interpolate(player.posY, player.prevPosY, partialTicks) - ReflectUtil.getRenderPosY());
			float z = (float) (interpolate(player.posZ, player.prevPosZ, partialTicks) - ReflectUtil.getRenderPosZ());
			glTranslated(x, y, z);

			boolean sneaking = player.isSneaking();

			float rotationYawHead = player.rotationYawHead;
			float renderYawOffset = player.renderYawOffset;
			float prevRenderYawOffset = player.prevRenderYawOffset;

			float xOff = interpolate(renderYawOffset, prevRenderYawOffset, partialTicks);
			float yOff = sneaking ? 0.6F : 0.75F;

			glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
			glTranslatef(0.0F, 0.0F, sneaking ? -0.235F : 0.0F);

			// draw limbs with rotation
			drawLimbs(entPos, yOff, sneaking, xOff, rotationYawHead);

			glPopMatrix();
		}
	}

	private void drawLimbs(float[][] entPos, float yOff, boolean sneaking, float xOff, float rotationYawHead) {
		// draw arms
		for (int i = 1; i <= 2; i++) {
			drawArm(entPos[i + 2], i == 1 ? -0.125F : 0.125F, yOff);
		}

		glTranslatef(0.0F, 0.0F, sneaking ? 0.25F : 0.0F);
		glPushMatrix();
		glTranslatef(0.0F, sneaking ? -0.05F : 0.0F, sneaking ? -0.01725F : 0.0F);

		// draw right and left arm
		for (int i = 1; i <= 2; i++) {
			drawLimb(entPos[i], i == 1 ? -0.375F : 0.375F, yOff + 0.55F);
		}

		// handle head position
		glRotatef(xOff - rotationYawHead, 0.0F, 1.0F, 0.0F);
		drawHead(entPos[0], yOff);

		glPopMatrix();

		// draw spine and other body parts
		drawSpine(yOff);
	}

	private void drawArm(float[] rotations, float xOffset, float yOff) {
		glPushMatrix();
		glTranslatef(xOffset, yOff, 0.0F);
		applyRotations(rotations);
		glBegin(GL_LINE_STRIP);
		glVertex3i(0, 0, 0);
		glVertex3f(0.0F, -yOff, 0.0F);
		glEnd();
		glPopMatrix();
	}

	private void drawLimb(float[] rotations, float xOffset, float yOff) {
		glPushMatrix();
		glTranslatef(xOffset, yOff, 0.0F);
		applyRotations(rotations);
		glBegin(GL_LINE_STRIP);
		glVertex3i(0, 0, 0);
		glVertex3f(0.0F, -0.5F, 0.0F);
		glEnd();
		glPopMatrix();
	}

	private void drawHead(float[] rotations, float yOff) {
		glPushMatrix();
		glTranslatef(0.0F, yOff + 0.55F, 0.0F);
		applyRotations(rotations);
		glBegin(GL_LINE_STRIP);
		glVertex3i(0, 0, 0);
		glVertex3f(0.0F, 0.3F, 0.0F);
		glEnd();
		glPopMatrix();
	}

	private void applyRotations(float[] rotations) {
		if (rotations[0] != 0.0F) {
			glRotatef(rotations[0] * DEGREES_IN_RADIAN, 1.0F, 0.0F, 0.0F);
		}
		if (rotations[1] != 0.0F) {
			glRotatef(rotations[1] * DEGREES_IN_RADIAN, 0.0F, 1.0F, 0.0F);
		}
		if (rotations[2] != 0.0F) {
			glRotatef(rotations[2] * DEGREES_IN_RADIAN, 0.0F, 0.0F, 1.0F);
		}
	}

	private void drawSpine(float yOff) {
		glPushMatrix();
		glTranslated(0.0F, yOff, 0.0F);
		glBegin(GL_LINE_STRIP);
		glVertex3f(-0.125F, 0.0F, 0.0F);
		glVertex3f(0.125F, 0.0F, 0.0F);
		glEnd();
		glPopMatrix();

		glPushMatrix();
		glTranslatef(0.0F, yOff, 0.0F);
		glBegin(GL_LINE_STRIP);
		glVertex3i(0, 0, 0);
		glVertex3f(0.0F, 0.55F, 0.0F);
		glEnd();
		glPopMatrix();

		glPushMatrix();
		glTranslatef(0.0F, yOff + 0.55F, 0.0F);
		glBegin(GL_LINE_STRIP);
		glVertex3f(-0.375F, 0.0F, 0.0F);
		glVertex3f(0.375F, 0.0F, 0.0F);
		glEnd();
		glPopMatrix();
	}

	public void drawImage(int image, float x, float y, float width, float height, int color) {
		enableGL2D();
		glPushMatrix();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
		glEnable(GL11.GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);
		glEnable(GL11.GL_ALPHA_TEST);
		GlStateManager.enableBlend();
		GlStateManager.bindTexture(image);

		ColorUtil.color(color);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); // top left
		GL11.glVertex2f(x, y);

		GL11.glTexCoord2f(0, 1); // bottom left
		GL11.glVertex2f(x, y + height);

		GL11.glTexCoord2f(1, 1); // bottom right
		GL11.glVertex2f(x + width, y + height);

		GL11.glTexCoord2f(1, 0); // top right
		GL11.glVertex2f(x + width, y);
		GL11.glEnd();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.resetColor();

		glEnable(GL_CULL_FACE);
		glPopMatrix();
		disableGL2D();
	}

	public void drawBloomShadow(float x, float y, float width, float height, int blurRadius, Color color,
			boolean scissor) {
		drawBloomShadow(x, y, width, height, blurRadius, 0, color.getRGB(), scissor, false, false, false, false);
	}

	public void drawBloomShadow(float x, float y, float width, float height, int blurRadius, int color,
			boolean scissor) {
		drawBloomShadow(x, y, width, height, blurRadius, 0, color, scissor, false, false, false, false);
	}

	public void drawBloomShadow(float x, float y, float width, float height, int blurRadius, int roundRadius,
			int color, boolean scissor) {
		drawBloomShadow(x, y, width, height, blurRadius, roundRadius, color, scissor, false, false, false, false);
	}

	public void drawBloomShadow(float x, float y, float width, float height, int blurRadius, int roundRadius, int color, boolean scissor, boolean cut_top, boolean cut_bottom, boolean cut_left, boolean cut_right) {
		width = width + blurRadius * 2;
		height = height + blurRadius * 2;
		x -= blurRadius + 0.75f;
		y -= blurRadius + 0.75f;

		int identifier = Arrays.deepHashCode(new Object[] { width, height, blurRadius, roundRadius });
		if (!shadowCache.containsKey(identifier)) {
			if (width <= 0)
				width = 1;
			if (height <= 0)
				height = 1;
			BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics g = original.getGraphics();
			g.setColor(new Color(-1));
			g.fillRoundRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2), roundRadius, roundRadius);
			g.dispose();
			GaussianFilter op = new GaussianFilter(blurRadius);
			BufferedImage blurred = op.filter(original, null);
			int cut_x = blurRadius, cut_y = blurRadius, cut_w = (int) (width - blurRadius * 2),
					cut_h = (int) (height - blurRadius * 2);
			if (cut_top) {
				cut_y = 0;
				cut_h = (int) (height - blurRadius);
			}

			if (cut_bottom) {
				cut_h = (int) (height - blurRadius);
			}

			if (cut_left) {
				cut_x = 0;
				cut_w = (int) (width - blurRadius);
			}

			if (cut_right) {
				cut_w = (int) (width - blurRadius);
			}
			if (scissor)
				blurred = new ShaderScissor(cut_x, cut_y, cut_w, cut_h, blurred, 1, false, false).generate();
			shadowCache.put(identifier,
					TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false));
		}
		drawImage(shadowCache.get(identifier), x, y, width, height, color);
	}

	public void disableGL2D() {
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
	}

	public void enableGL2D() {
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
	}

	public void drawTriangle(float cx, float cy, float r, float n, Color color) {
		GL11.glPushMatrix();
		cx *= 2.0;
		cy *= 2.0;
		double b = 6.2831852 / n;
		double p = Math.cos(b);
		double s = Math.sin(b);
		r *= 2.0;
		double x = r;
		double y = 0.0;
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthMask(true);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		GlStateManager.color(0, 0, 0);
		GlStateManager.resetColor();
		ColorUtil.color(color);
		GL11.glBegin(2);
		int ii = 0;
		while (ii < n) {
			GL11.glVertex2d(x + cx, y + cy);
			double t = x;
			x = p * x - s * y;
			y = s * t + p * y;
			ii++;
		}
		GL11.glEnd();
		GL11.glScalef(2f, 2f, 2f);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glHint(3154, 4352);
		GL11.glHint(3155, 4352);
		GlStateManager.color(1, 1, 1, 1);
		GL11.glPopMatrix();
	}

	public float getExtraWidth() {
		if (mc.gameSettings.thirdPersonView == 0) {
			return 2;
		}
		return 1;
	}

	public void drawRectangle(float x, float y, float width, float height, float lineWidth, boolean filled) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x - 0.05f, y - 0.15f, 0.0f);

		if (filled) {
			GL11.glLineWidth(lineWidth * getExtraWidth());
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2f(-width / 2, -height / 2);
			GL11.glVertex2f(width / 2, -height / 2);
			GL11.glVertex2f(width / 2, height / 2);
			GL11.glVertex2f(-width / 2, height / 2);
			GL11.glEnd();
		} else {
			GL11.glLineWidth(lineWidth * getExtraWidth());
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex2f(-width / 2, -height / 2);
			GL11.glVertex2f(width / 2, -height / 2);
			GL11.glVertex2f(width / 2, height / 2);
			GL11.glVertex2f(-width / 2, height / 2);
			GL11.glEnd();
		}
		GL11.glPopMatrix();
	}

	public void drawStar(float x, float y, float radius) {
		final int POINTS = 5;
		final float[] angles = new float[POINTS * 2];

		for (int i = 0; i < POINTS * 2; i++) {
			angles[i] = (float) Math.toRadians(i * 360.0f / (POINTS * 2) - 90.0f);
		}

		float[] vertices = new float[POINTS * 4];
		float innerRadius = radius * 0.6f;

		for (int i = 0; i < POINTS * 2; i++) {
			float angle = angles[i];
			float currentRadius = (i % 2 == 0) ? radius : innerRadius;
			vertices[i * 2] = x + (float) Math.cos(angle) * currentRadius;
			vertices[i * 2 + 1] = y + (float) Math.sin(angle) * currentRadius;
		}

		GL11.glBegin(GL11.GL_LINE_LOOP);
		for (int i = 0; i < POINTS * 2; i++) {
			GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
		}
		GL11.glEnd();
	}

	public void drawStar(float centerX, float centerY, float radius, float rotationOffset) {
		int points = 5;
		double angleIncrement = Math.PI / points;
		GL11.glBegin(GL11.GL_LINE_LOOP);

		for (int i = 0; i < points * 2; i++) {
			double angle = i * angleIncrement + rotationOffset;
			float scale = (i % 2 == 0) ? 1.0f : 0.5f;
			GL11.glVertex2f(centerX + (float) Math.cos(angle) * radius * scale,
					centerY + (float) Math.sin(angle) * radius * scale);
		}
		GL11.glEnd();
	}

	public void drawTriangle(float x, float y, float base, float height, float rotationAngle) {
		float[] vertices = new float[6];

		vertices[0] = -base / 2;
		vertices[1] = 0;

		vertices[2] = base / 2;
		vertices[3] = 0;

		vertices[4] = 0;
		vertices[5] = height;

		GL11.glPushMatrix();

		GL11.glTranslatef(x, y, 0);

		GL11.glRotatef(rotationAngle, 0, 0, 1);

		for (int i = 0; i < 3; i++) {
			GL11.glVertex2f(vertices[i * 2], vertices[i * 2 + 1]);
		}
		GL11.glEnd();

		GL11.glPopMatrix();
	}

	public void drawCircle(float radius, float lineWidth) {
		GL11.glLineWidth(lineWidth * getExtraWidth());
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for (int i = 0; i <= 360; i += 5) {
			float angle = (float) Math.toRadians(i);
			GL11.glVertex2f((float) Math.cos(angle) * radius, (float) Math.sin(angle) * radius);
		}
		GL11.glEnd();
	}

	public void drawDecorativeCircle(float radius, float lineWidth) {
		GL11.glLineWidth(lineWidth * getExtraWidth());
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for (int i = 0; i <= 360; i += 5) {
			float angle = (float) Math.toRadians(i);
			float x = (float) Math.cos(angle) * radius;
			float y = (float) Math.sin(angle) * radius;

			GL11.glVertex2f(x, y);

			if (i % 90 == 0) {
				drawSegment(x, y, angle, 0.1f, 0.03f);
			}
		}
		GL11.glEnd();
	}

	public void drawSegment(float x, float y, float angle, float offset, float inwardOffset) {
		GL11.glVertex2f(x + (float) Math.cos(angle) * offset, y + (float) Math.sin(angle) * offset);
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x - (float) Math.cos(angle) * inwardOffset, y - (float) Math.sin(angle) * inwardOffset);
		GL11.glVertex2f(x, y);
	}

	public void drawCircleWithOffsets(float radius, float offset, float inwardOffset, float lineWidth) {
		GL11.glLineWidth(lineWidth * getExtraWidth());
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for (int i = 0; i <= 360; i += 5) {
			float angle = (float) Math.toRadians(i);
			float x = (float) Math.cos(angle) * radius;
			float y = (float) Math.sin(angle) * radius;

			GL11.glVertex2f(x, y);

			if (i % 90 == 0) {
				GL11.glVertex2f(x + (float) Math.cos(angle) * offset, y + (float) Math.sin(angle) * offset);
				GL11.glVertex2f(x, y);
				GL11.glVertex2f(x - (float) Math.cos(angle) * inwardOffset, y - (float) Math.sin(angle) * inwardOffset);
				GL11.glVertex2f(x, y);
			}
		}
		GL11.glEnd();
	}

	public void drawCircle(float radius, float lineWidth, Color color) {
		ColorUtil.color(color);
		GL11.glLineWidth(lineWidth * getExtraWidth());
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for (int i = 0; i <= 360; i += 5) {
			float angle = (float) Math.toRadians(i);
			GL11.glVertex2f((float) Math.cos(angle) * radius, (float) Math.sin(angle) * radius);
		}
		GL11.glEnd();
	}

	public void drawArc(float radius, int startAngle, int endAngle, float lineWidth, Color color) {
		ColorUtil.color(color);
		GL11.glLineWidth(lineWidth * getExtraWidth());
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for (int i = startAngle; i <= endAngle; i += 5) {
			float angle = (float) Math.toRadians(i);
			GL11.glVertex2f((float) Math.cos(angle) * radius, (float) Math.sin(angle) * radius);
		}
		GL11.glEnd();
	}

	public void drawLineExtensions(float radius, float extensionLength, int[] angles) {
		GL11.glLineWidth(4.0f * getExtraWidth());
		GL11.glBegin(GL11.GL_LINES);
		for (int angle : angles) {
			float angleRad = (float) Math.toRadians(angle);
			GL11.glVertex3f((float) Math.cos(angleRad) * radius, (float) Math.sin(angleRad) * radius, 0.0f);
			GL11.glVertex3f((float) Math.cos(angleRad) * (radius + extensionLength),
					(float) Math.sin(angleRad) * (radius + extensionLength), 0.0f);
		}
		GL11.glEnd();
	}

	public void drawHaloRectangles() {
		ColorUtil.color(new Color(161, 253, 228, 220));
		drawRectangle(0.20f, 0.02f, 0.26f, 0.26f, 4f, false);
		drawRectangle(0.2f, 0.3f, 0.4f, 0.4f, 6f, false);
		drawRectangle(-0.09f, 0.21f, 0.35f, 0.35f, 5f, false);
		drawRectangle(-0.13f, 0.45f, 0.15f, 0.05f, 4f, false);
		drawRectangle(0.12f, 0.49f, 0.1f, 0f, 6f, false);
	}

	private double interpolate(final double current, final double previous, final double multiplier) {
		return previous + (current - previous) * multiplier;
	}

	private float interpolate(final float current, final float previous, final float multiplier) {
		return previous + (current - previous) * multiplier;
	}

	public void drawBlock(BlockPos blockPos, Color color, boolean slice) {
		double x = blockPos.getX() - (mc.getRenderManager()).viewerPosX;
		double y = blockPos.getY() - (mc.getRenderManager()).viewerPosY;
		double z = blockPos.getZ() - (mc.getRenderManager()).viewerPosZ;
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(3553);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		if (slice) {
			drawAroundAxis(new AxisAlignedBB(x, y, z, x + 1.0D, y + 0.564D, z + 1.0D), color);
		} else {
			drawAroundAxis(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), color);
		}
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDepthMask(true);
		GL11.glDisable(3042);
	}

	public void drawAroundAxis(AxisAlignedBB axis, Color color) {
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		float r = color.getRed() / 255.0F;
		float g = color.getGreen() / 255.0F;
		float b = color.getBlue() / 255.0F;
		float a = color.getAlpha() / 255.0F;
		wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
		wr.pos(axis.minX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		tess.draw();
		wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
		wr.pos(axis.minX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		tess.draw();
		wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
		wr.pos(axis.minX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		tess.draw();
		wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
		wr.pos(axis.maxX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		tess.draw();
		wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
		wr.pos(axis.minX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		tess.draw();
		wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
		wr.pos(axis.minX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.minX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.minZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.maxY, axis.maxZ).color(r, g, b, a).endVertex();
		wr.pos(axis.maxX, axis.minY, axis.maxZ).color(r, g, b, a).endVertex();
		tess.draw();
	}
}