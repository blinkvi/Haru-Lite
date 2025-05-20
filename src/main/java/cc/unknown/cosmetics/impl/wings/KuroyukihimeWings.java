package cc.unknown.cosmetics.impl.wings;

import static cc.unknown.util.render.WingsUtil.SCALE;
import static cc.unknown.util.render.WingsUtil.centreOffset;
import static cc.unknown.util.render.WingsUtil.getWingAngle;
import static cc.unknown.util.render.WingsUtil.setRotation;
import static cc.unknown.util.render.WingsUtil.wingScale;

import org.lwjgl.opengl.GL11;

import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.enums.CosmeticType;
import cc.unknown.util.render.enums.WingsType;
import cc.unknown.util.structure.vectors.Vector3d;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class KuroyukihimeWings extends ModelBase implements LayerRenderer<AbstractClientPlayer>, Accessor {
	private final RenderPlayer playerRenderer;
    ModelRenderer rightWing;
    ModelRenderer leftWing;

	public KuroyukihimeWings(RenderPlayer playerRendererIn) {

		this.playerRenderer = playerRendererIn;
        textureWidth = 256;
        textureHeight = 64;
        
        leftWing = new ModelRenderer(this, 0, 0);
        leftWing.addBox(-62.5F, 0F, -0.5F, 125, 59, 1);
        leftWing.setRotationPoint(0F, 0F, 0F);
        leftWing.setTextureSize(256, 64);
        
        rightWing = new ModelRenderer(this, 0, 0);
        rightWing.addBox(-62.5F, 0F, -0.5F, 125, 59, 1);
        rightWing.setRotationPoint(0F, 0F, 0F);
        rightWing.setTextureSize(256, 64);
        rightWing.mirror = true;
	}


	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scalee) {
		if (!entitylivingbaseIn.isInvisible() && CosmeticController.shouldRenderCosmeticForPlayer(entitylivingbaseIn, CosmeticType.WINGS, "Kuroyukihime")) {
	        float angle = getWingAngle(entitylivingbaseIn, 40, 8000, 250);
	        
	        setRotation(leftWing, new Vector3d(Math.toRadians(angle + 20), Math.toRadians(0), Math.toRadians(0)));
	        setRotation(rightWing, new Vector3d(Math.toRadians(-angle - 20), Math.toRadians(0), Math.toRadians(0)));

	        GL11.glPushMatrix();
	        GL11.glTranslatef(0, 4 * SCALE, 1.9F * SCALE);
	        GL11.glRotatef(90, 0, 1, 0);
	        GL11.glRotatef(90, 0, 0, 1);
            String imagePath = WingsType.KUROYUKIHIME.getImagePath();					
            mc.getTextureManager().bindTexture(new ResourceLocation(imagePath));
	        float scale = 0.32F;
	        GL11.glTranslatef(0F, SCALE * scale * (wingScale - 1), 0F);
	        
	        GL11.glScalef(scale, scale, scale);
	        
	        GL11.glPushMatrix();
	        GL11.glTranslatef(0, 0, centreOffset * 3 * SCALE);
	        GL11.glScalef(wingScale, wingScale, wingScale);
	        leftWing.render(SCALE);
	        GL11.glPopMatrix();
	        
	        GL11.glPushMatrix();
	        GL11.glTranslatef(0, 0, -centreOffset * 3 * SCALE);
	        GL11.glScalef(wingScale, wingScale, wingScale);
	        rightWing.render(SCALE);
	        GL11.glPopMatrix();
	        
	        GL11.glPopMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}