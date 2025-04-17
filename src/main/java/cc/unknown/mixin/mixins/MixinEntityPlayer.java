package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cc.unknown.mixin.impl.IEntityPlayer;
import cc.unknown.util.client.system.StopWatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase implements IEntityPlayer {
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
	public StopWatch hideSneakHeight = new StopWatch();

	@Overwrite
	public float getEyeHeight() {
		float f = 1.62F;

		if (this.isPlayerSleeping()) {
			f = 0.2F;
		}

		if (this.isSneaking() && hideSneakHeight.finished(100)) {
			f -= 0.08F;
		}

		return f;
	}
	
	@Override
	public StopWatch getHideSneakHeight() {
		return hideSneakHeight;
	}
}
