package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.handlers.SpoofHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

@SuppressWarnings("all")
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	@Shadow
	private float equippedProgress;
	@Shadow
	private float prevEquippedProgress;
	@Shadow
	private ItemStack itemToRender;
	
	@Shadow
	private int equippedItemSlot = -1;

	@Final
	@Shadow
	private Minecraft mc;

	@Shadow
	protected abstract void func_178109_a(AbstractClientPlayer var1);

	@Shadow
	protected abstract void func_178105_d(float var1);

	@Shadow
	protected abstract void func_178098_a(float var1, AbstractClientPlayer var2);

	@Shadow
	protected abstract void func_178110_a(EntityPlayerSP var1, float var2);

	@Shadow
	protected abstract void func_178104_a(AbstractClientPlayer var1, float var2);

	@Shadow
	protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

	@Shadow
	protected abstract void func_178095_a(AbstractClientPlayer var1, float var2, float var3);

	@Shadow
	public abstract void renderItem(EntityLivingBase var1, ItemStack var2, TransformType var3);

	@Shadow
	protected abstract void func_178101_a(float var1, float var2);

	@Shadow
	protected abstract void renderItemMap(AbstractClientPlayer var1, float var2, float var3, float var4);

	@Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemModelForEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"))
	public void renderItem(EntityLivingBase entity, ItemStack item, TransformType transformType, CallbackInfo ci) {
		if (!(item.getItem() instanceof ItemSword)) return;
		if (!(entity instanceof EntityPlayer)) return;
		if (!(((EntityPlayer) entity).getItemInUseCount() > 0)) return;
		if (transformType != TransformType.THIRD_PERSON) return;
		GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.04F, -0.04F, 0.0F);
	}

	@Overwrite
	private void func_178103_d() {
		GlStateManager.translate(-0.5F, 0.2F, 0.0F);
		GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
	}

	@Overwrite
	public void renderItemInFirstPerson(float partialTicks) {
		float f = 1.0F - (prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTicks);
		EntityPlayerSP player = mc.thePlayer;
		float f1 = player.getSwingProgress(partialTicks);
		float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
		float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
		func_178101_a(f2, f3);
		func_178109_a(player);
		func_178110_a(player, partialTicks);
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		
		if (itemToRender != null) {
			if (itemToRender.getItem() instanceof ItemMap) {
				renderItemMap(player, f2, f, f1);
			} else if (player.getItemInUseCount() > 0) {
				EnumAction enumaction = itemToRender.getItemUseAction();

				switch (enumaction) {
				case NONE:
					transformFirstPersonItem(f, 0.0F);
					break;
				case EAT:
				case DRINK:
					func_178104_a(player, partialTicks);
					transformFirstPersonItem(f, f1);
					break;
				case BLOCK:
					transformFirstPersonItem(f, f1);
					func_178103_d();
					break;
				case BOW:
					transformFirstPersonItem(f, f1);
					func_178098_a(partialTicks, player);
				}
			} else {
				func_178105_d(f1);
				transformFirstPersonItem(f, f1);
			}

			renderItem(player, itemToRender, TransformType.FIRST_PERSON);
		} else if (!player.isInvisible()) {
			func_178095_a(player, f, f1);
		}

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}

	@Overwrite
	public void renderOverlays(float partialTicks) {
	}

	@Overwrite
	private void renderWaterOverlayTexture(float partialTicks) {
	}

	@Overwrite
	public void updateEquippedItem() {
		this.prevEquippedProgress = this.equippedProgress;
		EntityPlayer entityplayer = this.mc.thePlayer;
		ItemStack itemstack = SpoofHandler.getSpoofedStack();
		boolean flag = false;

		if (this.itemToRender != null && itemstack != null) {
			if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
				if (!this.itemToRender.getItem().shouldCauseReequipAnimation(this.itemToRender, itemstack, equippedItemSlot != SpoofHandler.getSpoofedSlot())) {
					this.itemToRender = itemstack;
					this.equippedItemSlot = SpoofHandler.getSpoofedSlot();;
					return;
				}
				flag = true;
			}
		} else if (this.itemToRender == null && itemstack == null) {
			flag = false;
		} else {
			flag = true;
		}

		float f = 0.4F;
		float f1 = flag ? 0.0F : 1.0F;
		float f2 = MathHelper.clamp_float(f1 - this.equippedProgress, -f, f);
		this.equippedProgress += f2;

		if (this.equippedProgress < 0.1F) {
			this.itemToRender = itemstack;
			this.equippedItemSlot = SpoofHandler.getSpoofedSlot();;
		}
	}
}
