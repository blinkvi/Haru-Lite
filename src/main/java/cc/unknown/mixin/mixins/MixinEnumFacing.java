package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.EnumFacing;

@Mixin(EnumFacing.class)
public class MixinEnumFacing {

    @Shadow @Final
    public static EnumFacing[] VALUES;

    @Shadow @Final
    private int opposite;

    @Overwrite
    public EnumFacing getOpposite() {
        return VALUES[this.opposite];
    }

}