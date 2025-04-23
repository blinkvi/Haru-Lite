package cc.unknown.module.impl.move;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.StopWatch;
import cc.unknown.util.player.BlockUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.util.player.move.RotationUtil;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Clutch", description = "", category = Category.MOVE)
public class Clutch extends Module {

	private final SliderValue searchDist = new SliderValue("Search Distance", this, 4, 1, 30, 0.5f);

	private int lastSlot;
	
	private final StopWatch stopWatch = new StopWatch();

	@Override
	public void onEnable() {
		lastSlot = -1;
		stopWatch.reset();
	}

	@Override
	public void onDisable() {
		mc.thePlayer.inventory.currentItem = lastSlot;
		SpoofHandler.stopSpoofing();
		stopWatch.reset();
		PlayerUtil.rightClick(false);
	}

	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (lastSlot == -1) {
			lastSlot = mc.thePlayer.inventory.currentItem;
		}

		final int slot = InventoryUtil.findBlock();

		if (slot == -1) {
			return;
		}

		mc.thePlayer.inventory.currentItem = slot;
		SpoofHandler.startSpoofing(lastSlot);

		if (!(PlayerUtil.isOverVoid() && PlayerUtil.checkAir(mc.thePlayer.posX + mc.thePlayer.motionX * 7,
				mc.thePlayer.posY + MoveUtil.predictedSumMotion(mc.thePlayer.motionY, 7),
				mc.thePlayer.posZ + mc.thePlayer.motionZ * 7)) || mc.thePlayer.onGround) {
			return;
		}

		if (startSearch() && pickBlock()) {
	        if (stopWatch.hasPassed(1000 / 25) ) {
	            PlayerUtil.rightClick(true);
	            stopWatch.reset();
	        }
		}
	}

	private boolean startSearch() {
	    BlockPos below = new BlockPos(
	            mc.thePlayer.posX,
	            mc.thePlayer.posY - 2,
	            mc.thePlayer.posZ);

	    if (!BlockUtil.isReplaceable(below)) return false;

	    List<BlockPos> searchQueue = new ArrayList<>();
	    searchQueue.add(below.down());
	    int dist = (int) searchDist.getValue();

	    for (int x = -dist; x <= dist; x++) {
	        for (int z = -dist; z <= dist; z++) {
	            searchQueue.add(below.add(x, 0, z));
	        }
	    }

	    searchQueue.sort(Comparator.comparingDouble(BlockUtil::getClutchPriority));

	    for (BlockPos block : searchQueue) {
	        if (searchBlock(block)) {
	            return true;
	        }
	    }

	    for (BlockPos block : searchQueue) {
	        if (searchBlock(block.down())) {
	            return true;
	        }
	    }

	    for (BlockPos block : searchQueue) {
	        if (searchBlock(block.down().down())) {
	            return true;
	        }
	    }

	    return false;
	}

	private boolean searchBlock(BlockPos block) {
	    if (!BlockUtil.isReplaceable(block)) {
	        EnumFacing placeFace = BlockUtil.getHorizontalFacingEnum(block);

	        if (block.getY() <= mc.thePlayer.posY - 3) {
	            placeFace = EnumFacing.UP;
	        }

	        BlockPos blockPlacement = block.add(placeFace.getDirectionVec());
	        if (!BlockUtil.isReplaceable(blockPlacement)) {
	            return false;
	        }

	        double futurePosX = mc.thePlayer.posX;
	        double futurePosY = mc.thePlayer.posY;
	        double futurePosZ = mc.thePlayer.posZ;

	        mc.thePlayer.posX = futurePosX;
	        mc.thePlayer.posY = futurePosY;
	        mc.thePlayer.posZ = futurePosZ;

	        RotationUtil.setPlayerRotation(BlockUtil.getFaceRotation(placeFace, block));

	        PlayerUtil.rightClick(true);

	        return true;
	    }
	    return false;
	}

    private boolean pickBlock() {
        int slot = InventoryUtil.pickHotarBlock(true);
        if (slot != -1) {
            mc.thePlayer.inventory.currentItem = slot;
            return true;
        }
        return false;
    }
}