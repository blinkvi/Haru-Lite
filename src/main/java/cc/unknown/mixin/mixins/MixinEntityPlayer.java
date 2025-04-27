package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.JumpEvent;
import cc.unknown.event.impl.PostPlayerTickEvent;
import cc.unknown.event.impl.PrePlayerTickEvent;
import cc.unknown.util.client.system.Clock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {
	@Shadow
	public abstract ItemStack getHeldItem();

	@Shadow
	public abstract boolean isUsingItem();

	@Shadow
	public abstract FoodStats getFoodStats();

	@Shadow
	protected int flyToggleTimer;
	
	@Shadow
	public PlayerCapabilities capabilities;

	@Shadow
	public abstract boolean isPlayerSleeping();
	
	@Unique
	public Clock hideSneakHeight = new Clock();
	
	@Inject(method = "jump", at = @At("HEAD"))
    public void preJump(CallbackInfo ci) {
		Haru.eventBus.handle(new JumpEvent());
	}
	
	@Inject(method = "onUpdate", at = @At("HEAD"))
	public void prePlayerTick(CallbackInfo ci) {
		Haru.eventBus.handle(new PrePlayerTickEvent());
	}
	
	@Inject(method = "onUpdate", at = @At("RETURN"))
	public void postPlayerTick(CallbackInfo ci) {
		Haru.eventBus.handle(new PostPlayerTickEvent());
	}

	@Overwrite
	public float getEyeHeight() {
		float f = 1.62F;

		if (this.isPlayerSleeping()) {
			f = 0.2F;
		}

		if (this.isSneaking() && hideSneakHeight.hasPassed(100)) {
			f -= 0.08F;
		}

		return f;
	}
}
