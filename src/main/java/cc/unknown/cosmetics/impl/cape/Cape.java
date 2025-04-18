package cc.unknown.cosmetics.impl.cape;

import cc.unknown.cosmetics.CosmeticBase;
import cc.unknown.cosmetics.CosmeticController;
import cc.unknown.util.render.enums.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class Cape extends CosmeticBase {	
	private final RenderPlayer renderPlayer;

	public Cape(RenderPlayer renderPlayer) {
		super(renderPlayer);
        this.renderPlayer = renderPlayer;
    }

	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {		
		if (CosmeticController.shouldRenderCosmeticForPlayer(player, CosmeticType.CAPE) && !player.isInvisible() && player.isWearing(EnumPlayerModelParts.CAPE)) {
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        
	        String activeCape = isCape(player.getName());

	        switch (activeCape) {
	            case "Canada":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/canada.png"));
	                break;
	            case "France":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/france.png"));
	                break;
	            case "Germany":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/germany.png"));
	                break;
	            case "India":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/india.png"));
	                break;
	            case "Indonesia":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/indonesia.png"));
	                break;
	            case "Italy":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/italy.png"));
	                break;
	            case "Japan":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/japan.png"));
	                break;
	            case "Korean":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/korean.png"));
	                break;
	            case "UnitedKingdom":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/uk.png"));
	                break;
	            case "UnitedStates":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/flag/us.png"));
	                break;
	            case "Arcade":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/arcade.png"));
	                break;
	            case "Boost":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/boost.png"));
	                break;
	            case "Dark":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/dark.png"));
	                break;
	            case "Eyes":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/eyes.png"));
	                break;
	            case "Flame":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/flame.png"));
	                break;
	            case "Kocho":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/kocho.png"));
	                break;
	            case "ZeroTwo":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/custom/zero.png"));
	                break;
	            case "Minecon2011":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/minecon/2011.png"));
	                break;
	            case "Minecon2012":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/minecon/2012.png"));
	                break;
	            case "Minecon2013":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/minecon/2013.png"));
	                break;
	            case "Minecon2015":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/minecon/2015.png"));
	                break;
	            case "Minecon2016":
	                renderPlayer.bindTexture(new ResourceLocation("haru/cape/minecon/2016.png"));
	                break;
	            case "None":
	            default: break;
	        }

	        GlStateManager.pushMatrix();
	        GlStateManager.translate(0.0F, 0.0F, 0.125F);
	        double d0 = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * (double) partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks);
	        double d1 = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * (double) partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks);
	        double d2 = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * (double) partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks);
	        float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
	        double d3 = (double) MathHelper.sin(f * (float) Math.PI / 180.0F);
	        double d4 = (double) (-MathHelper.cos(f * (float) Math.PI / 180.0F));
	        float f1 = (float) d1 * 10.0F;
	        f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
	        float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
	        float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

	        if (f2 < 0.0F) {
	            f2 = 0.0F;
	        }

	        float f4 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
	        f1 = f1 + MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

	        if (player.isSneaking()) {
	            f1 += 25.0F;
	        }

	        if (player.isSneaking()) {
	            GlStateManager.translate(0.0F, 0.142F, -0.0178F);
	        }

	        GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
	        GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
	        GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
	        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
	        renderPlayer.getMainModel().renderCape(0.0625F);
	        GlStateManager.popMatrix();
	    }
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}