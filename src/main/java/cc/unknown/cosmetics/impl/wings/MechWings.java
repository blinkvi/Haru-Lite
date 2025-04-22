package cc.unknown.cosmetics.impl.wings;

import org.lwjgl.opengl.GL11;
import static cc.unknown.util.render.WingsUtil.*;

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
public class MechWings extends ModelBase implements LayerRenderer<AbstractClientPlayer>, Accessor {
	private final RenderPlayer playerRenderer;
    ModelRenderer leftWing;
    ModelRenderer rightWing;
    private final ResourceLocation[] wingsImage;

	public MechWings(RenderPlayer playerRendererIn) {

		this.playerRenderer = playerRendererIn;
        textureWidth = 64;
        textureHeight = 32;
        
        leftWing = new ModelRenderer(this, 0, 0);
        leftWing.addBox(-13.5F, 0F, -0.5F, 27, 25, 1);
        leftWing.setTextureSize(64, 32);
        
        rightWing = new ModelRenderer(this, 0, 0);
        rightWing.addBox(-13.5F, 0F, -0.5F, 27, 25, 1);
        rightWing.setTextureSize(64, 32);
        rightWing.mirror = true;

        wingsImage = new ResourceLocation[2];
        wingsImage[0] = new ResourceLocation("haru/cosmes/wings/mech.png");
        wingsImage[1] = new ResourceLocation("haru/cosmes/wings/mechColour.png");
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks,float p_177141_5_, float p_177141_6_, float p_177141_7_, float scalee) {
	    if (!entitylivingbaseIn.isInvisible() &&
	        CosmeticController.shouldRenderCosmeticForPlayer(entitylivingbaseIn, CosmeticType.WINGS)) {

	        if (isWings(entitylivingbaseIn.getName()).equalsIgnoreCase("Mech")) {
	            float angle = getWingAngle(entitylivingbaseIn, 40, 8000, 500);

	            setRotation(leftWing, new Vector3d(Math.toRadians(angle + 20), Math.toRadians(-4), Math.toRadians(6)));
	            setRotation(rightWing, new Vector3d(Math.toRadians(-angle - 20), Math.toRadians(4), Math.toRadians(6)));

	            GL11.glPushMatrix();
	            GL11.glTranslatef(0, 4 * SCALE, 1.5F * SCALE);
	            GL11.glRotatef(90, 0, 1, 0);
	            GL11.glRotatef(90, 0, 0, 1);

	            GL11.glPushMatrix();
	            GL11.glTranslatef(0, 0, centreOffset * 3 * SCALE);
	            GL11.glScalef(wingScale, wingScale, wingScale);

	            for (int i = 0; i < wingsImage.length; i++) {
	                mc.getTextureManager().bindTexture(wingsImage[i]);

	                if (i == 1) {
	                    GlStateManager.enableBlend();
	                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	                    GlStateManager.color(1F, 1F, 1F, 0.75F);
	                }

	                leftWing.render(SCALE);

	                if (i == 1) {
	                    GlStateManager.disableBlend();
	                    GlStateManager.color(1F, 1F, 1F, 1F);
	                }
	            }

	            GL11.glPopMatrix();

	            GL11.glPushMatrix();
	            GL11.glTranslatef(0, 0, -centreOffset * 3 * SCALE);
	            GL11.glScalef(wingScale, wingScale, wingScale);

	            for (int i = 0; i < wingsImage.length; i++) {
	                mc.getTextureManager().bindTexture(wingsImage[i]);

	                if (i == 1) { // glow layer
	                    GlStateManager.enableBlend();
	                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	                    GlStateManager.color(1F, 1F, 1F, 0.75F);
	                }

	                rightWing.render(SCALE);

	                if (i == 1) {
	                    GlStateManager.disableBlend();
	                    GlStateManager.color(1F, 1F, 1F, 1F);
	                }
	            }

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