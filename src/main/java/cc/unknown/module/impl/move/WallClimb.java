package cc.unknown.module.impl.move;

import org.lwjgl.input.Mouse;

import cc.unknown.event.player.BlockAABBEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "WallClimb", description = "", category = Category.MOVE)
public class WallClimb extends Module {
	
	public final Bool fast = new Bool("Fast", this, true);
	public final Slider mouseButton = new Slider("MouseButton", this, 1, 0, 5);
	
    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
		boolean isInsideBlock = insideBlock();
		
		if (mc.thePlayer.isCollidedHorizontally && !isInsideBlock) {
			double yaw = MoveUtil.direction();
			mc.thePlayer.setPosition(mc.thePlayer.posX + -MathHelper.sin((float) yaw) * 0.05, mc.thePlayer.posY, mc.thePlayer.posZ + MathHelper.cos((float) yaw) * 0.05);
			MoveUtil.stop();
			MoveUtil.keybindStop();
		}

		Mouse.poll();
		
		if (Mouse.isButtonDown(mouseButton.getAsInt()) && isInsideBlock) {
			if (fast.get()) {
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionY += 0.65999D;
				} else {
					mc.thePlayer.motionY -= 0.005D;
				}
			} else {
				if (mc.thePlayer.onGround) {
					mc.thePlayer.motionY += 0.64456D;
				} else {
					mc.thePlayer.motionY -= 0.005D;
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockAABB(BlockAABBEvent event) {
		if (insideBlock()) {
			BlockPos playerPos = new BlockPos(mc.thePlayer);
			BlockPos blockPos = event.blockPos;
			if (blockPos.getY() > playerPos.getY())
				event.boundingBox = null;
		}
	}

	private boolean insideBlock(final AxisAlignedBB bb) {
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)))) != null && bb.intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean insideBlock() {
		if (mc.thePlayer.ticksExisted < 5) {
			return false;
		}

		return insideBlock(mc.thePlayer.getEntityBoundingBox());
	}
}
