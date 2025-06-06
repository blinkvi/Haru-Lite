package cc.unknown.module.impl.move;

import cc.unknown.event.player.BlockAABBEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.event.player.PushOutOfBlockEvent;
import cc.unknown.event.render.Render2DEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.player.BlockUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.font.FontUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoClip", description = "", category = Category.MOVE)
public class NoClip extends Module {

	private int lastSlot;
	
	@Override
	public void onEnable() {
		lastSlot = -1;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.noClip = false;
		mc.thePlayer.inventory.currentItem = lastSlot;
		SpoofHandler.stopSpoofing();
		PlayerUtil.rightClick(false);
	}
	
	@SubscribeEvent
	public void onBlockAABB(BlockAABBEvent event) {
		if (!BlockUtil.insideBlock()) return;

		Block block = event.block;
		BlockPos pos = event.blockPos;
		double x = pos.getX(), y = pos.getY(), z = pos.getZ();
		AxisAlignedBB expandedBox = AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15);

		boolean isSneaking = mc.gameSettings.keyBindSneak.isKeyDown();
		boolean isAirBlock = block instanceof BlockAir;

		event.boundingBox = null;

		if (!isSneaking) {

			if (y < mc.thePlayer.posY) {
				event.boundingBox = expandedBox.offset(x, y, z);
			}

			if (!isAirBlock) {
				event.boundingBox = expandedBox.offset(x, y, z);
			}
		}
	}
	
	@SubscribeEvent
	public void onPushOutOfBlock(PushOutOfBlockEvent event) {
		event.setCanceled(true);
	}

    @SubscribeEvent
    public void onPreAttack(PrePositionEvent event) {
		if (lastSlot == -1) {
			lastSlot = mc.thePlayer.inventory.currentItem;
		}

		mc.thePlayer.noClip = true;
		
		final int slot = InventoryUtil.findBlock();

		if (slot == -1 || BlockUtil.insideBlock()) {
			return;
		}

		mc.thePlayer.inventory.currentItem = slot;
		SpoofHandler.startSpoofing(lastSlot);
		
		if (mc.thePlayer.rotationPitch >= 45 && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.thePlayer.posY == mc.objectMouseOver.getBlockPos().up().getY()) {
			PlayerUtil.rightClick(true);
		}
	}

	@SubscribeEvent
	public void onRender2D(Render2DEvent event) {
		FontUtil.getFontRenderer("comfortaa.ttf", 17).drawCenteredString("press shift", event.width() / 2F, event.height() - 90, getModule(Interface.class).color());
	}
}