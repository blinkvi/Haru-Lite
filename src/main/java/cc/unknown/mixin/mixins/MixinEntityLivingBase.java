package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.Haru;
import cc.unknown.event.player.JumpEvent;
import cc.unknown.module.impl.visual.AntiDebuff;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

	@Shadow
	@Final
	private ItemStack[] previousEquipment = new ItemStack[5];
	
	@Shadow
	private BaseAttributeMap attributeMap;
	
	@Shadow
	protected float prevOnGroundSpeedFactor;
	
	@Shadow
	public abstract Vec3 getLook(float partialTicks);
	
	@Shadow
	public float renderYawOffset;
	
	@Shadow
	public int arrowHitTimer;
	
	@Shadow
	protected float movedDistance;
	
	@Shadow
	protected float onGroundSpeedFactor;
	
	@Shadow
	public float prevRenderYawOffset;
	
	@Shadow
    public float rotationYawHead;

	@Shadow
    public float prevRotationYawHead;
	
	@Shadow
	public float swingProgress;
	
	@Shadow
    public float moveStrafing;
    
	@Shadow
	public float moveForward;

	@Shadow
	public abstract CombatTracker getCombatTracker();
	
	@Shadow
	protected abstract float getJumpUpwardsMotion();

	@Shadow
	public abstract boolean isPotionActive(Potion potionIn);

	@Shadow
	public abstract PotionEffect getActivePotionEffect(Potion potionIn);
	
    @Shadow
    public void onLivingUpdate() { }
	
	@Shadow
	public abstract void setLastAttacker(Entity entityIn);
	
	@Shadow
	public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);
	
	@Shadow
	public abstract void setSprinting(boolean sprinting);
	
	@Shadow
	public abstract boolean isOnLadder();
	
	@Shadow
	public abstract boolean isChild();
	
	@Shadow
    public float prevLimbSwingAmount;
	
	@Shadow
    public float limbSwingAmount;
	
	@Shadow
	public float limbSwing;
	
	@Overwrite
	protected void jump() {
		float jumpMotion = this.getJumpUpwardsMotion();

		if (this.isPotionActive(Potion.jump)) {
			jumpMotion += (float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
		}

		if ((Object) this == Minecraft.getMinecraft().thePlayer) {
			final JumpEvent event = new JumpEvent(jumpMotion, this.rotationYaw);
			MinecraftForge.EVENT_BUS.post(event);
			jumpMotion = event.getJumpMotion();
			this.rotationYaw = event.getYaw();

			if (event.isCanceled()) {
				return;
			}
		}

		this.motionY = jumpMotion;

		if (this.isSprinting()) {
			float f = this.rotationYaw * 0.017453292F;
			this.motionX -= MathHelper.sin(f) * 0.2F;
			this.motionZ += MathHelper.cos(f) * 0.2F;
		}

		this.isAirBorne = true;
	}

    @Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
    private void isPotionActive(Potion potion, CallbackInfoReturnable<Boolean> ci) {
        AntiDebuff antiDebuff = Haru.instance.getModuleManager().getModule(AntiDebuff.class);
        if (antiDebuff.isEnabled() && potion == Potion.blindness || potion == Potion.confusion) {
        	ci.setReturnValue(false);
        }
    }
}