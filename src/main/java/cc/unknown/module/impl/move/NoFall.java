package cc.unknown.module.impl.move;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.value.impl.Bool;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "NoFall", description = "", category = Category.MOVE)
public class NoFall extends Module {

	private final Bool switchToItem = new Bool("SwitchToItem", this, true);

	private float prevPitch;

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (event.phase != Phase.END && isInGame() && !mc.isGamePaused()) {
			MovingObjectPosition rayCast = wrayCast(mc.playerController.getBlockReachDistance(), mc.thePlayer.rotationYaw, 90);
			if (inPosition() && rayCast != null && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && holdWaterBucket(switchToItem.get()) || inPosition() && rayCast != null && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && holdSpiderweb(switchToItem.get())) {
				prevPitch = mc.thePlayer.rotationPitch;
				mc.thePlayer.rotationPitch = 90;
				PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
			}
			
			if (mc.thePlayer.onGround && mc.thePlayer.isInWater()) {
				PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
				mc.thePlayer.rotationPitch = prevPitch;
			}
		}
	}

	private boolean inPosition() {
		if (mc.thePlayer.motionY < -0.6D && !mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.capabilities.isCreativeMode) {
			BlockPos playerPos = mc.thePlayer.getPosition();

			for (int i = 1; i < 3; ++i) {
				BlockPos blockPos = playerPos.down(i);
				Block block = mc.theWorld.getBlockState(blockPos).getBlock();
				if (block.isBlockSolid(mc.theWorld, blockPos, EnumFacing.UP)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean holdWaterBucket(boolean setSlot) {
		if (this.containsItem(mc.thePlayer.getHeldItem(), Items.water_bucket)) {
			return true;
		} else {
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
				if (this.containsItem(mc.thePlayer.inventory.mainInventory[i], Items.water_bucket) && setSlot) {
					mc.thePlayer.inventory.currentItem = i;
					return true;
				}
			}

			return false;
		}
	}

	private boolean holdSpiderweb(boolean setSlot) {
		if (this.containsItem(mc.thePlayer.getHeldItem(), Item.getItemFromBlock(Blocks.web))) {
			return true;
		} else {
			for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
				if (this.containsItem(mc.thePlayer.inventory.mainInventory[i], Item.getItemFromBlock(Blocks.web)) && setSlot) {
					mc.thePlayer.inventory.currentItem = i;
					return true;
				}
			}

			return false;
		}
	}

	private boolean containsItem(ItemStack itemStack, Item item) {
		return itemStack != null && itemStack.getItem() == item;
	}

	private MovingObjectPosition wrayCast(final double n, final float n2, final float n3) {
		final Vec3 getPositionEyes = mc.thePlayer.getPositionEyes(1.0f);
		final float n4 = -n2 * 0.017453292f;
		final float n5 = -n3 * 0.017453292f;
		final float cos = MathHelper.cos(n4 - 3.1415927f);
		final float sin = MathHelper.sin(n4 - 3.1415927f);
		final float n6 = -MathHelper.cos(n5);
		final Vec3 vec3 = new Vec3((double) (sin * n6), (double) MathHelper.sin(n5), (double) (cos * n6));
		return mc.theWorld.rayTraceBlocks(getPositionEyes, getPositionEyes.addVector(vec3.xCoord * n, vec3.yCoord * n, vec3.zCoord * n), false, false, false);
	}
}