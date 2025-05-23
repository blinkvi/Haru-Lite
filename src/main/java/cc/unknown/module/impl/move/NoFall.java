package cc.unknown.module.impl.move;

import java.util.OptionalInt;
import java.util.stream.IntStream;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "NoFall", description = "Use water bucket.", category = Category.MOVE)
public class NoFall extends Module {

	private boolean handling;

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (event.phase != Phase.END && isInGame() && !mc.isGamePaused()) {
			ItemStack heldItem = mc.thePlayer.getHeldItem();

			if (mc.thePlayer.dimension == -1)
				toggle();

			if (inPosition() && holdWaterBucket()) {
				handling = true;
			}

			if (handling) {
				if (containsItem(heldItem, Items.water_bucket) && mc.thePlayer.rotationPitch >= 70.0F) {
					MovingObjectPosition object = mc.objectMouseOver;
					if (object.typeOfHit == MovingObjectType.BLOCK && object.sideHit == EnumFacing.UP) {
						mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
					}
				}
				if (mc.thePlayer.onGround || mc.thePlayer.motionY > 0.0D) {
					if (containsItem(heldItem, Items.bucket)) {
						mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
					}

					handling = false;
				}
			}

		}
	}

	private boolean inPosition() {
		if (mc.thePlayer.motionY < -0.6D && !mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.capabilities.isCreativeMode && !handling) {
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

	private boolean holdWaterBucket() {
		if (containsItem(mc.thePlayer.getHeldItem(), Items.water_bucket)) {
			return true;
		}

		OptionalInt slot = IntStream.range(0, InventoryPlayer.getHotbarSize()).filter(i -> containsItem(mc.thePlayer.inventory.mainInventory[i], Items.water_bucket)).findFirst();

		if (slot.isPresent()) {
			mc.thePlayer.inventory.currentItem = slot.getAsInt();
			return true;
		}

		return false;
	}


	private boolean containsItem(ItemStack stack, Item item) {
		return stack != null && stack.getItem() == item;
	}
}