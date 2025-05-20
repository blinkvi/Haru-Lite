package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.util.player.EnumFacings;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

@Mixin(World.class)
public abstract class MixinWorld {
	
	@Shadow
	public abstract IBlockState getBlockState(BlockPos pos);
	
	@Shadow
	public abstract boolean checkNoEntityCollision(AxisAlignedBB bb, Entity entityIn);

	@Overwrite
	public boolean canBlockBePlaced(final Block blockIn, final BlockPos pos, final boolean p_175716_3_, final EnumFacing side, final Entity entityIn, final ItemStack itemStackIn) {
		final Block block = this.getBlockState(pos).getBlock();
		final AxisAlignedBB axisalignedbb = p_175716_3_ ? null : blockIn.getCollisionBoundingBox((World) (Object)this, pos, blockIn.getDefaultState());
		return (axisalignedbb == null || Minecraft.getMinecraft().thePlayer.noClip || this.checkNoEntityCollision(axisalignedbb, entityIn)) && (block.getMaterial() == Material.circuits && blockIn == Blocks.anvil || block.getMaterial().isReplaceable() && blockIn.canReplace((World) (Object)this, pos, side, itemStackIn));
	}
	
    @Redirect(method = "getHorizon", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getHorizon()D", remap = false))
    private double alwaysZero(WorldProvider worldProvider) {
        return 0.0D;
    }
    
    @ModifyArg(method = "checkLightFor", at = @At(value="INVOKE", target="Lnet/minecraft/world/World;isAreaLoaded(Lnet/minecraft/util/BlockPos;IZ)Z", ordinal=0), index=1)
    public int reduceAreaLoadedCheckRange(int radius) {
        return 16;
    }
    
    @Redirect(method = "getRawLight", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] getRawLight$getCachedArray() {
        return EnumFacings.FACINGS;
    }

    @Redirect(method = "checkLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] checkLightFor$getCachedArray() {
        return EnumFacings.FACINGS;
    }

    @Redirect(method = "isBlockIndirectlyGettingPowered", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] isBlockIndirectlyGettingPowered$getCachedArray() {
        return EnumFacings.FACINGS;
    }
}
