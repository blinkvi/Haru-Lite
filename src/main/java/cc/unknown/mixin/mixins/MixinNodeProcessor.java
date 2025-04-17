package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

@Mixin(NodeProcessor.class)
public class MixinNodeProcessor {

	@Shadow
	protected IBlockAccess blockaccess;

	@Inject(method = "postProcess", at = @At("HEAD"))
	private void cleanupBlockAccess(CallbackInfo ci) {
		this.blockaccess = null;
	}
}