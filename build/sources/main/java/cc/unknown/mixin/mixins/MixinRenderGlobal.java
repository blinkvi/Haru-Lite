package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.util.player.EnumFacings;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.EnumFacing;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

	@Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
	private EnumFacing[] setupTerrain$getCachedArray() {
		return EnumFacings.FACINGS;
	}
}
