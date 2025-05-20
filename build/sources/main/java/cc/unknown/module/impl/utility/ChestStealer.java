package cc.unknown.module.impl.utility;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "ChestStealer", description = "", category = Category.UTILITY)
public class ChestStealer extends Module {

	private final Slider startDelay = new Slider("StartDelay", this, 2, 0, 10);
	private final Slider minDelay = new Slider("MinDelay", this, 1, 0, 10);
	private final Slider maxDelay = new Slider("MinDelay", this, 1, 0, 10);
	private final Bool ignoreJunk = new Bool("IgnoreJunk", this, true);
	private final Bool autoClose = new Bool("AutoClose", this, true);
	private final Bool checkName = new Bool("ChestName", this, true);

	private final Clock clock = new Clock();
	private final Clock startTimer = new Clock();

	private int decidedTimer = 0;

	public static boolean closeAfterContainer;

	private boolean gotItems;
	private int ticksInChest;

	private boolean lastInChest;
	
	@Override
	public void onEnable() {
		correctValues(minDelay, maxDelay);
	}
	
	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
		if (event.phase == Phase.START) return;
		correctValues(minDelay, maxDelay);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (!lastInChest) {
			startTimer.reset();
		}

		lastInChest = mc.currentScreen instanceof GuiChest;

		if (mc.currentScreen instanceof GuiChest) {
			if (checkName.get()) {
				final String name = ReflectUtil.isLowerChestInventory().getDisplayName().getUnformattedText();

				if (!name.toLowerCase().contains("chest")) {
					return;
				}
			}

			if (!startTimer.hasPassedTicks((int) startDelay.getValue()))
				return;

			if (decidedTimer == 0) {
				final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue(), maxDelay.getValue()));
				final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue(), maxDelay.getValue()));
				decidedTimer = MathUtil.randomInt(delayFirst, delaySecond);
			}

			if (clock.hasPassedTicks(decidedTimer)) {
				final ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

				boolean randomize = false;

				if (randomize) {
					boolean found = false;
					for (int i = 0; i < chest.inventorySlots.size(); i++) {
						final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);

						if (stack != null && (itemWhitelisted(stack) && ignoreJunk.get())) {
							found = true;
						}
					}

					int i = 0;
					while (chest.getLowerChestInventory().getStackInSlot(i) == null) {
						i = MathUtil.randomInt(1, chest.inventorySlots.size());
						break;
					}

					final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);

					if (stack != null && (itemWhitelisted(stack) && ignoreJunk.get())) {
						mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
						clock.reset();
						final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue(), maxDelay.getValue()));
						final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue(), maxDelay.getValue()));
						decidedTimer = MathUtil.randomInt(delayFirst, delaySecond);
						gotItems = true;
						return;
					}

					if (gotItems && !found && autoClose.get() && ticksInChest > 3) {
						mc.thePlayer.closeScreen();
						return;
					}
				} else {
					for (int i = 0; i < chest.inventorySlots.size(); i++) {
						final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);

						if (stack != null && (itemWhitelisted(stack) && ignoreJunk.get())) {
							mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
							clock.reset();
							final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue(), maxDelay.getValue()));
							final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue(), maxDelay.getValue()));
							decidedTimer = MathUtil.randomInt(delayFirst, delaySecond);
							gotItems = true;
							return;
						}
					}

					if (gotItems && autoClose.get() && ticksInChest > 3) {
						mc.thePlayer.closeScreen();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (mc.currentScreen instanceof GuiChest && Display.isVisible() && (!checkName.get() || (ReflectUtil.isLowerChestInventory().getDisplayName().getUnformattedText().contains("chest")))) {
			mc.mouseHelper.mouseXYChange();
			mc.mouseHelper.ungrabMouseCursor();
			mc.mouseHelper.grabMouseCursor();
		}

		if (mc.currentScreen instanceof GuiChest) {
			ticksInChest++;

			if (ticksInChest * 50 > 255) {
				ticksInChest = 10;
			}
		} else {
			ticksInChest--;
			gotItems = false;

			if (ticksInChest < 0) {
				ticksInChest = 0;
			}
		}
	}

	private boolean itemWhitelisted(final ItemStack itemStack) {
		final ArrayList<Item> whitelistedItems = new ArrayList<Item>() {
			private static final long serialVersionUID = -4063268508656626059L;

			{
				add(Items.ender_pearl);
				add(Items.iron_ingot);
				add(Items.snowball);
				add(Items.gold_ingot);
				add(Items.redstone);
				add(Items.diamond);
				add(Items.emerald);
				add(Items.quartz);
				add(Items.bow);
				add(Items.arrow);
				add(Items.fishing_rod);
			}
		};
		final Item item = itemStack.getItem();
		final String itemName = itemStack.getDisplayName();

		if (itemName.contains("Right Click") || itemName.contains("Click to Use")
				|| itemName.contains("Players Finder")) {
			return true;
		}

		final ArrayList<Integer> whitelistedPotions = new ArrayList<Integer>() {
			private static final long serialVersionUID = -5531907215825315299L;

			{
				add(6);
				add(1);
				add(5);
				add(8);
				add(14);
				add(12);
				add(10);
				add(16);
			}
		};

		if (item instanceof ItemPotion) {
			final int potionID = getPotionId(itemStack);
			return whitelistedPotions.contains(potionID);
		}

		return (item instanceof ItemBlock && !(((ItemBlock) item).getBlock() instanceof BlockTNT)
				&& !(((ItemBlock) item).getBlock() instanceof BlockSlime)
				&& !(((ItemBlock) item).getBlock() instanceof BlockFalling)) || item instanceof ItemAnvilBlock
				|| item instanceof ItemSword || item instanceof ItemArmor || item instanceof ItemTool
				|| item instanceof ItemFood || item instanceof ItemSkull || itemName.contains("\247")
				|| whitelistedItems.contains(item) && !item.equals(Items.spider_eye);
	}

	private int getPotionId(final ItemStack potion) {
		final Item item = potion.getItem();

		try {
			if (item instanceof ItemPotion) {
				final ItemPotion p = (ItemPotion) item;
				return p.getEffects(potion.getMetadata()).get(0).getPotionID();
			}
		} catch (final NullPointerException ignored) {
		}

		return 0;
	}

}