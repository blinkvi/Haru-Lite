package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.event.render.Render2DEvent;
import cc.unknown.util.render.client.CameraUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

	@Shadow
	private Minecraft mc;

	@Shadow
	private float thirdPersonDistance = 4.0F;

	@Shadow
	private float thirdPersonDistanceTemp = 4.0F;

	@Shadow
	private boolean cloudFog;

	@Overwrite
	public void orientCamera(float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();
		float f = entity.getEyeHeight();
		double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
		double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
			f = (float) ((double) f + 1.0D);
			GlStateManager.translate(0.0F, 0.3F, 0.0F);

			if (!this.mc.gameSettings.debugCamEnable) {
				BlockPos blockpos = new BlockPos(entity);
				IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
				net.minecraftforge.client.ForgeHooksClient.orientBedCamera(this.mc.theWorld, blockpos, iblockstate,
						entity);

				GlStateManager.rotate(
						entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F,
						0.0F, -1.0F, 0.0F);
				GlStateManager.rotate(
						entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
						-1.0F, 0.0F, 0.0F);
			}
		} else if (this.mc.gameSettings.thirdPersonView > 0) {
			double d3 = (double) (this.thirdPersonDistanceTemp
					+ (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);

			if (this.mc.gameSettings.debugCamEnable) {
				GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
			} else {
				float f1 = entity.rotationYaw;
				float f2 = entity.rotationPitch;

				if (this.mc.gameSettings.thirdPersonView == 2) {
					f2 += 180.0F;
				}

				double d4 = (double) (-MathHelper.sin(f1 / 180.0F * (float) Math.PI)
						* MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
				double d5 = (double) (MathHelper.cos(f1 / 180.0F * (float) Math.PI)
						* MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
				double d6 = (double) (-MathHelper.sin(f2 / 180.0F * (float) Math.PI)) * d3;

				for (int i = 0; i < 8; ++i) {
					float f3 = (float) ((i & 1) * 2 - 1);
					float f4 = (float) ((i >> 1 & 1) * 2 - 1);
					float f5 = (float) ((i >> 2 & 1) * 2 - 1);
					f3 = f3 * 0.1F;
					f4 = f4 * 0.1F;
					f5 = f5 * 0.1F;
					MovingObjectPosition movingobjectposition = this.mc.theWorld
							.rayTraceBlocks(new Vec3(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), new Vec3(
									d0 - d4 + (double) f3 + (double) f5, d1 - d6 + (double) f4, d2 - d5 + (double) f5));

					if (movingobjectposition != null) {
						double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));

						if (d7 < d3) {
							d3 = d7;
						}
					}
				}

				if (this.mc.gameSettings.thirdPersonView == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
				GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.translate(0.0F, 0.0F, -0.1F);
		}

		if (!this.mc.gameSettings.debugCamEnable) {
			if (CameraUtil.freelooking) {
				GlStateManager.rotate(CameraUtil.cameraPitch, 1f, 0f, 0f);
			} else {
				GlStateManager.rotate(
						entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
						1.0F, 0.0F, 0.0F);
			}

			if (entity instanceof EntityAnimal) {
				EntityAnimal entityanimal = (EntityAnimal) entity;
				GlStateManager.rotate(entityanimal.prevRotationYawHead
						+ (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F,
						0.0F, 1.0F, 0.0F);
			} else if (CameraUtil.freelooking) {
				GlStateManager.rotate(CameraUtil.cameraYaw, 0f, 1f, 0f);
			} else {
				GlStateManager.rotate(
						entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F,
						0.0F, 1.0F, 0.0F);
			}
		}

		GlStateManager.translate(0.0F, -f, 0.0F);
		d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
		d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
		this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
	}

	@Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V", shift = At.Shift.AFTER))
	private void injectRender2DEvent(float partialTicks, long nanoTime, CallbackInfo ci) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();

		MinecraftForge.EVENT_BUS.post(new Render2DEvent(new ScaledResolution(mc), partialTicks));
	}

	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setAngles(FF)V"))
	public void updateCameraAndRender(EntityPlayerSP player, float yaw, float pitch) {
		if (CameraUtil.freelooking) {
			CameraUtil.overrideMouse(yaw, pitch);
		} else {
			player.setAngles(yaw, pitch);
		}
	}
}