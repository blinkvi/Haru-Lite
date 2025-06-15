package cc.unknown.util.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.Clock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
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
	
    public static Clock inventoryStopWatch = new Clock();

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

    public static boolean isFull() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() == null) {
                return false;
            }
        }
        return true;
    }
	
    public static Item getItem() {
        ItemStack stack = getItemStack();
        return stack == null ? null : stack.getItem();
    }
    
    public static ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(mc.thePlayer.inventory.currentItem + 36).getStack());
    }

	public static boolean isSword() {
		return getItem() instanceof ItemSword;
	}
	
	public static boolean isBow() {
		return getItem() instanceof ItemBow;
	}
    
	public static boolean isFood() {
		return getItem() instanceof ItemFood;
	}
	
	public static boolean isDrink() {
		return getItem() instanceof ItemPotion || getItem() instanceof ItemBucketMilk;
	}
	
    public static boolean getProjectiles() {
    	return getItem() instanceof ItemExpBottle || getItem() instanceof ItemEgg || getItem() instanceof ItemSnowball;
    }
    
    public static boolean getAnyBlock() {
    	return getItem() instanceof ItemBlock;
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
	
	public static ItemStack bestSword() {
		ItemStack bestSword = null;
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemSword) {
					float swordDamage = itemDamage(is);
					if (swordDamage >= itemDamage) {
						itemDamage = itemDamage(is);
						bestSword = is;
					}
				}
			}
		}

		return bestSword;
	}
	
	public static ItemStack bestBow() {
		ItemStack bestBow = null;
		float itemDamage = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemBow) {
					float bowDamage = bowDamage(is);
					if (bowDamage >= itemDamage) {
						itemDamage = bowDamage(is);
						bestBow = is;
					}
				}
			}
		}

		return bestBow;
	}
	
	public static ItemStack bestAxe() {
		ItemStack bestTool = null;
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemAxe) {
					float toolSkill = toolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = toolRating(is);
						bestTool = is;
					}
				}
			}
		}

		return bestTool;
	}

	public static ItemStack bestPick() {
		ItemStack bestTool = null;
		float itemSkill = -1.0F;

		for (int i = 9; i < 45; ++i) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemPickaxe) {
					float toolSkill = toolRating(is);
					if (toolSkill >= itemSkill) {
						itemSkill = toolRating(is);
						bestTool = is;
					}
				}
			}
		}

		return bestTool;
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
                ReflectUtil.mouseClicked(x, y, mouseButton);
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
    
    public static boolean bestArmor(ItemStack stack, int type) {
		float prot = armorProtection(stack);
		String armor = "";
		
		switch (type) {
		case 1:
			armor = "helmet";
			break;
		case 2:
			armor = "chestplate";
			break;
		case 3:
			armor = "leggings";
			break;
		case 4:
			armor = "boots";
			break;
		}
		
		if (!stack.getUnlocalizedName().contains(armor)) {
			return false;
		} else {
			for (int i = 5; i < 45; ++i) {
				if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
					if (armorProtection(is) > prot && is.getUnlocalizedName().contains(armor)) {
						return false;
					}
				}
			}

			return true;
		}
	}
    
    public static float armorProtection(ItemStack stack) {
		float prot = 0.0F;
		if (stack.getItem() instanceof ItemArmor) {
			ItemArmor armor = (ItemArmor) stack.getItem();
			prot = (float) (prot + armor.damageReduceAmount + 100 - armor.damageReduceAmount * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D);
			prot =  prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100;
			prot = prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100;
			prot = prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100;
			prot = prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50;
			prot = prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) / 100;
		}

		return prot;
	}
    
    public static float itemDamage(ItemStack itemStack) {
		float damage = materialRating(itemStack, true);
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
		damage += (float) (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 1.0E-12F;
		if (itemStack.getItem() instanceof ItemSword) {
			damage = (float) (damage + 0.2D);
		}

		return damage;
	}
    
	public static float bowDamage(ItemStack itemStack) {
		float damage = 5.0F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) * 1.25F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.75F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.1F;
		damage += (float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage() * 0.001F;
		return damage;
	}
    
	public static float toolRating(ItemStack itemStack) {
		float damage = materialRating(itemStack, false);
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack) * 2.0F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) * 0.5F;
		damage += (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.1F;
		damage += (float) (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 1.0E-12F;
		return damage;
	}
    
    public static float materialRating(ItemStack itemStack, boolean checkForDamage) {
		if (itemStack == null || itemStack.getItem() == null) {
			return 0;
		}

		Item item = itemStack.getItem();
		String materialName = null;

		if (item instanceof ItemTool) {
			materialName = ((ItemTool) item).getToolMaterialName();
		} else if (item instanceof ItemSword) {
			materialName = ((ItemSword) item).getToolMaterialName();
		}

		if (materialName == null) {
			return 0.0F;
		}

		Map<String, Float> materialRatings = new HashMap<>();
		materialRatings.put("WOOD", 2.0F);
		materialRatings.put("STONE", 3.0F);
		materialRatings.put("IRON", 4.0F);
		materialRatings.put("GOLD", 2.0F);
		materialRatings.put("EMERALD", 5.0F);

		float baseRating = materialRatings.getOrDefault(materialName, 0.0F);

		if (item instanceof ItemSword) {
			baseRating += 2.0F;
		} else if (item instanceof ItemPickaxe || item instanceof ItemSpade) {
			baseRating = checkForDamage ? baseRating : baseRating * 10;
		} else if (item instanceof ItemAxe) {
			baseRating += 1.0F;
		}

		return baseRating;
	}

    public static boolean trash(ItemStack is, boolean preferSword, boolean keepTools) {
        if (is == null) return false;

        Item item = is.getItem();

        if (item instanceof ItemArmor) {
            String name = is.getUnlocalizedName();

            for (int type = 1; type <= 4; ++type) {
            	String armorPart = "";
            	
    			switch (type) {
    			case 1:
    				armorPart = "helmet";
    				break;
    			case 2:
    				armorPart = "chestplate";
    				break;
    			case 3:
    				armorPart = "leggings";
    				break;
    			case 4:
    				armorPart = "boots";
    				break;
    			}

                if (name.contains(armorPart)) {
                    boolean isBest = bestArmor(is, type);
                    ItemStack equipped = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    boolean hasEquipped = equipped != null && equipped.getUnlocalizedName().contains(armorPart);

                    if (!isBest || (hasEquipped && bestArmor(equipped, type))) {
                        return true;
                    }
                }
            }
        }

        if (item instanceof ItemSword && is != bestSword()) {
            return true;
        }

        if (item instanceof ItemBow && is != bestBow()) {
            return true;
        }

        if (item instanceof ItemAxe && (preferSword || !keepTools || is != bestAxe())) {
            return true;
        }

        if (item instanceof ItemPickaxe && (preferSword || !keepTools || is != bestPick())) {
            return true;
        }

        return false;
    }
}
