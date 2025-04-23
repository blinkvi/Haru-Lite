package cc.unknown.event.player;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockAABBEvent extends Event {
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
