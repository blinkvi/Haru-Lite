package cc.unknown.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
@Getter
@Setter
public class BlockAABBEvent extends Event {
	private final World world;
	private final Block block;
	private final BlockPos blockPos;
	private AxisAlignedBB boundingBox;
	private final AxisAlignedBB maskBoundingBox;
}
