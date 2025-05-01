package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.event.player.JumpEvent;
import cc.unknown.util.client.system.Clock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;

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
		MinecraftForge.EVENT_BUS.post(new JumpEvent());
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
