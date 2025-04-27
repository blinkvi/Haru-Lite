package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.BlockAABBEvent;
import cc.unknown.event.impl.PrePositionEvent;
import cc.unknown.event.impl.PushOutOfBlockEvent;
import cc.unknown.event.impl.Render2DEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.player.BlockUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.render.font.FontUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

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
	}
	
	@EventLink
	public final Listener<BlockAABBEvent> onBlockAABB = event -> {
		if (!BlockUtil.insideBlock()) return;

		Block block = event.block;
		BlockPos pos = event.blockPos;
		double x = pos.getX(), y = pos.getY(), z = pos.getZ();

		boolean isSneaking = mc.gameSettings.keyBindSneak.isKeyDown();
		boolean isAirBlock = block instanceof BlockAir;

		event.boundingBox = null;

		if (!isSneaking) {
			AxisAlignedBB expandedBox = AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15);

			if (y < mc.thePlayer.posY) {
				event.boundingBox = expandedBox.offset(x, y, z);
			}

			if (!isAirBlock) {
				event.boundingBox = expandedBox.offset(x, y, z);
			}
		}
	};
	
    @EventLink
    public final Listener<PushOutOfBlockEvent> onPushOutOfBlock = event -> event.setCanceled(true);

    @EventLink
    public final Listener<PrePositionEvent> onPrePosition = event -> {
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
			mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, InventoryUtil.getItemStack(), mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);

			mc.thePlayer.swingItem();
		}
	};
	
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> FontUtil.getFontRenderer("comfortaa.ttf", 17).drawCenteredString("presiona shift", event.getScaledWidth() / 2F, event.getScaledHeight() - 90, getModule(Interface.class).color());
}