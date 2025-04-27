package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.Random;

import org.lwjgl.input.Mouse;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AutoClicker", description = "Automatically clicks for you.", category = Category.COMBAT)
public class AutoClicker extends Module {

	private final ModeValue mode = new ModeValue("Mode", this, "Legit", "Legit", "Blatant");
	private final SliderValue minCPS = new SliderValue("MinCPS", this, 9, 1, 25, 0.5f);
	private final SliderValue maxCPS = new SliderValue("MaxCPS", this, 13, 1, 25, 0.5f);
	private final ModeValue randomize = new ModeValue("Randomize", this, "Render", "Render", "Tick"); // events

	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("Inventory", false),
			new BoolValue("OnlyWeapon", false),
			new BoolValue("BreakBlocks", false)));
	
	private long lastClick;
	private long leftHold;
	private boolean leftDown;
	private long leftDownTime;
	private long leftUpTime;
	private long leftk;
	private long leftl;
	private double leftm;
	private boolean leftn;
	private boolean breakHeld;

	private Random rand = null;

	@Override
	public void onEnable() {
		rand = new Random();
		correctValues(minCPS, maxCPS);
	}

	@Override
	public void onDisable() {
		leftDownTime = 0L;
		leftUpTime = 0L;
	}

	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
		if (event.phase == Phase.START) return;
		correctValues(minCPS, maxCPS);
	}
	
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)) return;

		if (!randomize.is("Render")) return;

		if (mode.is("Legit")) {
			ravenClick();
		} else if (mode.is("Blatant")) {
			skidClick(event, null);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)) return;

		if (!randomize.is("Tick")) return;

		if (mode.is("Legit")) {
			ravenClick();
		} else if (mode.is("Blatant")) {
			skidClick(null, event);
		}
	}

	@SubscribeEvent
	public void onPreAttack(PrePositionEvent event) {
		if (conditionals.isEnabled("Inventory") && mc.currentScreen instanceof GuiContainer) {
			InventoryUtil.guiClicker(mc.currentScreen, 1, getRandomizedCPS());
		}
	}

	private void skidClick(TickEvent.RenderTickEvent renderTick, TickEvent.PlayerTickEvent playerTick) {
		if (!isInGame()) return;

		double speedLeft1 = 1.0 / io.netty.util.internal.ThreadLocalRandom.current().nextDouble(minCPS.getValue() - 0.2D, maxCPS.getValue());
		double leftHoldLength = speedLeft1 / io.netty.util.internal.ThreadLocalRandom.current().nextDouble(minCPS.getValue() - 0.02D, maxCPS.getValue());
		Mouse.poll();

		if (Mouse.isButtonDown(0)) {
			if (breakBlock()) return;
			if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) {
				return;
			}

			double speedLeft = 1.0 / java.util.concurrent.ThreadLocalRandom.current().nextDouble(minCPS.getValue() - 0.2, maxCPS.getValue());
			if (System.currentTimeMillis() - lastClick > speedLeft * 1000) {
				lastClick = System.currentTimeMillis();
				if (leftHold < lastClick) {
					leftHold = lastClick;
				}
				
				int key = mc.gameSettings.keyBindAttack.getKeyCode();
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				PlayerUtil.setMouseButtonState(0, true);
			} else if (System.currentTimeMillis() - leftHold > leftHoldLength * 1000) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
				PlayerUtil.setMouseButtonState(0, false);
			}
		}
	}

	private void ravenClick() {
		Mouse.poll();
		if (!Mouse.isButtonDown(0) && !leftDown) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
			PlayerUtil.setMouseButtonState(0, false);
		}
		
		if (Mouse.isButtonDown(0) || leftDown) {
			if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) {
				return;
			}
			leftClickExecute(mc.gameSettings.keyBindAttack.getKeyCode());
		}
	}

	private void leftClickExecute(int key) {
		if (breakBlock()) return;

		if (leftUpTime > 0L && leftDownTime > 0L) {
			if (System.currentTimeMillis() > leftUpTime && leftDown) {
				KeyBinding.setKeyBindState(key, true);
				KeyBinding.onTick(key);
				genLeftTimings();
				PlayerUtil.setMouseButtonState(0, true);
				leftDown = false;
			} else if (System.currentTimeMillis() > leftDownTime) {
				KeyBinding.setKeyBindState(key, false);
				leftDown = true;
				PlayerUtil.setMouseButtonState(0, false);
			}
		} else {
			genLeftTimings();
		}

	}

	private void genLeftTimings() {
		double clickSpeed = ranModuleVal(minCPS, maxCPS, rand) + 0.4D * rand.nextDouble();
		long delay = (int) Math.round(1000.0D / clickSpeed);
		if (System.currentTimeMillis() > leftk) {
			if (!leftn && rand.nextInt(100) >= 85) {
				leftn = true;
				leftm = 1.1D + rand.nextDouble() * 0.15D;
			} else {
				leftn = false;
			}

			leftk = System.currentTimeMillis() + 500L + (long) rand.nextInt(1500);
		}

		if (leftn) {
			delay = (long) ((double) delay * leftm);
		}

		if (System.currentTimeMillis() > leftl) {
			if (rand.nextInt(100) >= 80) {
				delay += 50L + (long) rand.nextInt(100);
			}

			leftl = System.currentTimeMillis() + 500L + (long) rand.nextInt(1500);
		}

		leftUpTime = System.currentTimeMillis() + delay;
		leftDownTime = System.currentTimeMillis() + delay / 2L - (long) rand.nextInt(10);
	}

	private boolean breakBlock() {
		if (conditionals.isEnabled("BreakBlocks") && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();

			if (p != null) {
				Block bl = mc.theWorld.getBlockState(p).getBlock();
				if (bl != Blocks.air && !(bl instanceof BlockLiquid)) {
					if (!breakHeld) {
						int e = mc.gameSettings.keyBindAttack.getKeyCode();
						KeyBinding.setKeyBindState(e, true);
						KeyBinding.onTick(e);
						breakHeld = true;
					}
					return true;
				}
				if (breakHeld) {
					breakHeld = false;
				}
			}
		}
		return false;
	}

	private double ranModuleVal(SliderValue min, SliderValue max, Random r) {
		return min.getValue() == max.getValue() ? min.getValue()
				: min.getValue() + r.nextDouble() * (max.getValue() - min.getValue());
	}
	
    private int getRandomizedCPS() {
        int baseCPS = Math.round(25);
        int delta = Math.min(Math.round(2), 20);
        int minCPS = Math.max(1, baseCPS - delta);
        int maxCPS = baseCPS + delta;
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(minCPS, maxCPS + 1);
    }
}