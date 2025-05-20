package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.util.player.EnumFacings;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCache;

@Mixin(ChunkCache.class)
public class MixinChunkCache {

    @Redirect(method = "getLightForExt", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    private EnumFacing[] getCachedArray() {
        return EnumFacings.FACINGS;
    }
}