package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAABBEvent extends CancellableEvent {
	public final World world;
	public final Block block;
	public final BlockPos blockPos;
	public AxisAlignedBB boundingBox;
	public final AxisAlignedBB maskBoundingBox;

	public BlockAABBEvent(World world, Block block, BlockPos blockPos, AxisAlignedBB boundingBox, AxisAlignedBB maskBoundingBox) {
		this.world = world;
		this.block = block;
		this.blockPos = blockPos;
		this.boundingBox = boundingBox;
		this.maskBoundingBox = maskBoundingBox;
	}

}
