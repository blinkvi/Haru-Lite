package cc.unknown.cosmetics.impl.wings;

import org.lwjgl.opengl.GL11;
import static cc.unknown.util.render.WingsUtil.*;

import java.awt.Color;

import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.enums.CosmeticType;
import cc.unknown.util.render.enums.WingsType;
import cc.unknown.util.structure.vectors.Vector3d;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;

@SuppressWarnings("unused")
public class DemonWings extends ModelBase implements LayerRenderer<AbstractClientPlayer>, Accessor {
	private final RenderPlayer playerRenderer;
    ModelRenderer rightWing;
    ModelRenderer leftWing;

	public DemonWings(RenderPlayer playerRendererIn) {

		this.playerRenderer = playerRendererIn;
        textureWidth = 64;
        textureHeight = 32;

        leftWing = new ModelRenderer(this, 0, 0);
        leftWing.setTextureSize(64, 32);
        leftWing.addBox(-8.5F, 0F, -0.5F, 17, 30, 1);

        rightWing = new ModelRenderer(this, 0, 0);
        rightWing.setTextureSize(64, 32);
        rightWing.addBox(-8.5F, 0F, -0.5F, 17, 30, 1);
        rightWing.mirror = true;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scalee) {
	    if (!entitylivingbaseIn.isInvisible() &&
	        CosmeticController.shouldRenderCosmeticForPlayer(entitylivingbaseIn, CosmeticType.WINGS)) {

	        if (isWings(entitylivingbaseIn.getName()).equalsIgnoreCase("Demon")) {
	            float angle = getWingAngle(entitylivingbaseIn, 30, 5000, 400);

	            setRotation(leftWing, new Vector3d(Math.toRadians(angle + 20), Math.toRadians(-4), Math.toRadians(6)));
	            setRotation(rightWing, new Vector3d(Math.toRadians(-angle - 20), Math.toRadians(4), Math.toRadians(6)));
	            
	            GL11.glPushMatrix();
	            GL11.glTranslatef(0, 4 * SCALE, 1.5F * SCALE);
	            GL11.glRotatef(90, 0, 1, 0);
	            GL11.glRotatef(90, 0, 0, 1);
	            String imagePath = WingsType.DEMON.getImagePath();
	            mc.getTextureManager().bindTexture(new ResourceLocation(imagePath));
	            GL11.glColor3f(1.0f, 1.0f, 1.0f);
	            
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
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
	public String isWings(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getWings).filter(wings -> !wings.equalsIgnoreCase("None")).findFirst().orElse("None");
	}
}