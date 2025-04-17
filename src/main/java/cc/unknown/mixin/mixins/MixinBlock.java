package cc.unknown.mixin.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.event.player.BlockAABBEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Block.class)
public abstract class MixinBlock {

	@Shadow
	public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);

	@Overwrite
	public void addCollisionBoxesToList(final World worldIn, final BlockPos pos, final IBlockState state,
			final AxisAlignedBB mask, final List<AxisAlignedBB> list, final Entity collidingEntity) {
		final AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);

		if (collidingEntity == Minecraft.getMinecraft().thePlayer) {
			final BlockAABBEvent event = new BlockAABBEvent(worldIn, (Block) (Object) this, pos, axisalignedbb, mask);
			MinecraftForge.EVENT_BUS.post(event);

			if (event.isCanceled())
				return;

			if (event.getBoundingBox() != null && event.getMaskBoundingBox().intersectsWith(event.getBoundingBox())) {
				list.add(event.getBoundingBox());
			}
		} else {
			if (axisalignedbb != null && mask.intersectsWith(axisalignedbb)) {
				list.add(axisalignedbb);
			}
		}
	}
}