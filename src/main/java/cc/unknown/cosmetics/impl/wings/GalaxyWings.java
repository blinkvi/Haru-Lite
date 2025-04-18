package cc.unknown.cosmetics.impl.wings;

import org.lwjgl.opengl.GL11;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.cosmetics.CosmeticModelBase;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.enums.CosmeticType;
import cc.unknown.util.render.enums.WingsType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class GalaxyWings extends CosmeticBase {
	private final ModelGalaxyWings model;

	public GalaxyWings(RenderPlayer player) {
		super(player);
		this.model = new ModelGalaxyWings(player);
	}

	@Override
	public void render(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scalee) {
		if (!entitylivingbaseIn.isInvisible() && CosmeticController.shouldRenderCosmeticForPlayer(entitylivingbaseIn, CosmeticType.WINGS)) {
			if (isWings(entitylivingbaseIn.getName()).equalsIgnoreCase("Galaxy")) {
				double rotate = interpolate(entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset, partialTicks);
				GL11.glPushMatrix();
				GL11.glScaled(-0.6, -0.6, 0.6);
				GL11.glTranslated(0.0D, -1.45, 0.1D);
				GL11.glTranslated(0.0D, 1.3D, 0.2D);
				if (entitylivingbaseIn.isSneaking()) {
					GlStateManager.translate(0.0F, -0.142F, -0.0178F);
				}
				GL11.glRotated(130, 1, 0, 0);
				GL11.glRotated(180, 0, 1, 0);
	
				GL11.glColor3f(1, 1, 1);
				
	            String imagePath = WingsType.GALAXY.getImagePath();					
	            mc.getTextureManager().bindTexture(new ResourceLocation(imagePath));

				GL11.glColor3f(255.0F, 255.0F, 255.0F);
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
	static class ModelGalaxyWings extends CosmeticModelBase {
		
		private ModelRenderer wing;
		private ModelRenderer wingTip;

		public ModelGalaxyWings(RenderPlayer player) {
			super(player);
			setTextureOffset("wing.bone", 0, 0);
			setTextureOffset("wing.skin", -10, 8);
			setTextureOffset("wingtip.bone", 0, 5);
			setTextureOffset("wingtip.skin", -10, 18);
			this.wing = new ModelRenderer(this, "wing");
			this.wing.setTextureSize(30, 30);
			this.wing.setRotationPoint(-1.0F, 0.0F, 0.0F);
			this.wing.addBox("bone", -10.0F, -1.0F, -1.0F, 10, 2, 2);
			this.wing.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
			this.wingTip = new ModelRenderer(this, "wingtip");
			this.wingTip.setTextureSize(30, 30);
			this.wingTip.setRotationPoint(-10.0F, 0.0F, 0.0F);
			this.wingTip.addBox("bone", -10.0F, -0.5F, -0.5F, 10, 1, 1);
			this.wingTip.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
			this.wing.addChild(this.wingTip);
		}
		
		@Override
		public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
			for (int i = 0; i < 2; i++) {
				GL11.glEnable(GL11.GL_CULL_FACE);

				float f11 = (float) (System.currentTimeMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2.0F);

				this.wing.rotateAngleX = (float) Math.toRadians(-80.0D) - (float) Math.cos(f11) * 0.2F;
				this.wing.rotateAngleY = (float) Math.toRadians(20.0D) + (float) Math.sin(f11) * 0.4F;
				this.wing.rotateAngleZ = (float) Math.toRadians(20.0D);

				this.wingTip.rotateAngleZ = -((float) (Math.sin(f11 + 2.0F) + 0.5D)) * 0.95F;

				this.wing.render(0.0615F);
				GL11.glScalef(-1.0F, 1.0F, 1.0F);

				if (i == 0) {
					GL11.glCullFace(GL11.GL_FRONT);
				}
			}

			GL11.glCullFace(GL11.GL_BACK);
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			this.wing.render(scale);
			this.wingTip.render(scale);
		}
	}
	
	
	public String isWings(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getWings).filter(wings -> !wings.equalsIgnoreCase("None")).findFirst().orElse("None");
	}
	
	private float interpolate(float yaw1, float yaw2, float percent) {
		float f = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
		if (f < 0.0F)
			f += 360.0F;
		return f;
	}
}