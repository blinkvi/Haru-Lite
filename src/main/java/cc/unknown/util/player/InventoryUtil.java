package cc.unknown.util.player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.system.StopWatch;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;

public class InventoryUtil implements Accessor {

	public final static List<Block> blacklist = Arrays.asList(Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,
			Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.red_flower, Blocks.yellow_flower, Blocks.flower_pot,
			Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.light_weighted_pressure_plate,
			Blocks.heavy_weighted_pressure_plate, Blocks.jukebox, Blocks.air, Blocks.iron_bars,
			Blocks.stained_glass_pane, Blocks.ladder, Blocks.glass_pane, Blocks.carpet, Blocks.enchanting_table,
			Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch,
			Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate,
			Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.iron_door, Blocks.dropper, Blocks.tnt,
			Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch, Blocks.oak_door);
	
    public static StopWatch inventoryStopWatch = new StopWatch();

	public static int findBlock() {
		int slot = -1;
		int highestStack = -1;
		for (int i = 0; i < 9; ++i) {
			final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
			if (itemStack != null && itemStack.getItem() instanceof ItemBlock
					&& blacklist.stream().noneMatch(block -> block.equals(((ItemBlock) itemStack.getItem()).getBlock()))
					&& itemStack.stackSize > 0) {
				if (mc.thePlayer.inventory.mainInventory[i].stackSize > highestStack) {
					highestStack = mc.thePlayer.inventory.mainInventory[i].stackSize;
					slot = i;
				}
			}
		}
		return slot;
	}

	public static boolean isSword() {
		return getItem() instanceof ItemSword;
	}

	public static int findTool(final BlockPos blockPos) {
		float bestSpeed = 1;
		int bestSlot = -1;

		final IBlockState blockState = mc.theWorld.getBlockState(blockPos);

		for (int i = 0; i < 9; i++) {
			final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

			if (itemStack == null) {
				continue;
			}

			final float speed = itemStack.getStrVsBlock(blockState.getBlock());

			if (speed > bestSpeed) {
				bestSpeed = speed;
				bestSlot = i;
			}
		}

		return bestSlot;
	}

	public static void bestSword(Entity targetEntity) {
		int bestSlot = 0;
		float f = -1F;
		for (int i1 = 36; i1 < 45; i1++)
			if (mc.thePlayer.inventoryContainer.inventorySlots.toArray()[i1] != null && targetEntity != null) {
				ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
				if (curSlot != null && (curSlot.getItem() instanceof ItemSword)) {
					ItemSword sword = (ItemSword) curSlot.getItem();
					if (sword.getDamageVsEntity() > f) {
						bestSlot = i1 - 36;
						f = sword.getDamageVsEntity();
					}
				}
			}

		if (f > -1F) {
			mc.thePlayer.inventory.currentItem = bestSlot;
			mc.playerController.updateController();
		}
	}
	
    public static Item getItem() {
        ItemStack stack = getItemStack();
        return stack == null ? null : stack.getItem();
    }
    
    public static ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(mc.thePlayer.inventory.currentItem + 36).getStack());
    }
    
    public static boolean getProjectiles() {
    	return getItem() instanceof ItemExpBottle || getItem() instanceof ItemEgg || getItem() instanceof ItemSnowball;
    }
    
    public static boolean getAnyBlock() {
    	return getItem() instanceof ItemBlock;
    }
    
    public static Block getBlock() {
        ItemStack heldItem = getItemStack();
        if (heldItem == null) return Blocks.air;

        return Block.getBlockFromItem(heldItem.getItem());
    }
    
    public static void guiClicker(GuiScreen gui, int mouseButton, long delay) {
        if (!(gui instanceof GuiContainer)) return;

        if (Mouse.isButtonDown(mouseButton) && (Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42))) {
            long clickDelay = delay;

            if (inventoryStopWatch.hasPassed(clickDelay)) {
                int x = Mouse.getX() * gui.width / mc.displayWidth;
                int y = gui.height - Mouse.getY() * gui.height / mc.displayHeight - 1;

                try {
                    Method mouseClicked = GuiScreen.class.getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
                    mouseClicked.setAccessible(true);
                    mouseClicked.invoke(gui, x, y, mouseButton);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                inventoryStopWatch.reset();
            }
        }
    }
    
    public static int pickHotarBlock(boolean biggestStack) {
        if (biggestStack) {
            int currentStackSize = 0;
            int currentSlot = 36;
            for (int i = 36; i < 45; i++) {
                final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > currentStackSize) {
                    final Block block = ((ItemBlock) itemStack.getItem()).getBlock();

                    if (block.isFullCube() && !blacklist.contains(block)) {
                        currentStackSize = itemStack.stackSize;
                        currentSlot = i;
                    }
                }
            }

            if (currentStackSize > 0) {
                return currentSlot - 36;
            }
        } else {
            for (int i = 36; i < 45; i++) {
                final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
                    final Block block = ((ItemBlock) itemStack.getItem()).getBlock();

                    if (block.isFullCube() && !blacklist.contains(block))
                        return i - 36;
                }
            }
        }
        return -1;
    }
}
