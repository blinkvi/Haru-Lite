package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AutoClicker", description = "Automatically clicks for you.", category = Category.COMBAT)
public class AutoClicker extends Module {

	private final ModeValue mode = new ModeValue("Mode", this, "Legit", "Legit", "Blatant");
	private final SliderValue minCPS = new SliderValue("Min CPS", this, 9, 1, 25, 1);
	private final SliderValue maxCPS = new SliderValue("Max CPS", this, 13, 1, 25, 1);
	private final ModeValue randomize = new ModeValue("Randomize", this, "ButterFly", "ButterFly", "Jitter", "Drag");

	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("Inventory", false),
			new BoolValue("OnlyWeapon", false),
			new BoolValue("BreakBlocks", false)));

	private final Random random = new Random();

	private long lastClick;
	private long nextReleaseTime;
	private boolean leftDown;
	private long leftDownTime, leftUpTime, leftk, leftl;
	private double leftm;
	private boolean leftn;

	@Override
	public void onEnable() {
		correctValues(minCPS, maxCPS);
	}

	@Override
	public void onDisable() {
		leftDownTime = leftUpTime = 0L;
	}

	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
		if (event.phase == Phase.START) return;
		correctValues(minCPS, maxCPS);
	}

	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
		if (mc.currentScreen != null || !randomize.is("ButterFly")) return;
		handleClick(event, null, null);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (mc.currentScreen != null || !randomize.is("Drag")) return;
		handleClick(null, null, event);
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (mc.currentScreen != null || !randomize.is("Jitter")) return;
		handleClick(null, event, null);
	}

	private void handleClick(RenderWorldLastEvent worldEvent, TickEvent.PlayerTickEvent playerTick, TickEvent.RenderTickEvent renderTick) {
		if (mode.is("Legit")) {
			ravenClick();
		} else {
			skidClick(worldEvent, playerTick, renderTick);
		}
	}

	@SubscribeEvent
	public void onPreAttack(PrePositionEvent event) {
		if (conditionals.isEnabled("Inventory") && mc.currentScreen instanceof GuiContainer) {
			InventoryUtil.guiClicker(mc.currentScreen, 0, getRandomizedCPS());
		}
	}

	private void skidClick(RenderWorldLastEvent world, TickEvent.PlayerTickEvent player, TickEvent.RenderTickEvent render) {
		if (!isInGame() || !Mouse.isButtonDown(0) || breakBlock()) return;

		if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) return;

		double interval = 1.0 / MathUtil.randomDouble(minCPS.getValue() - 0.2, maxCPS.getValue());
		double hold = interval / MathUtil.randomDouble(minCPS.getValue() - 0.02, maxCPS.getValue());

		long currentTime = System.currentTimeMillis();

		if (currentTime - lastClick > interval * 1000) {
			lastClick = currentTime;
			if (nextReleaseTime < lastClick) {
				nextReleaseTime = lastClick;
			}
			click(true);
		} else if (currentTime - nextReleaseTime > hold * 1000) {
			click(false);
		}
	}

	private void ravenClick() {
		Mouse.poll();

		if (!Mouse.isButtonDown(0) && !leftDown) {
			click(false);
			return;
		}

		if (Mouse.isButtonDown(0) || leftDown) {
			if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) return;
			leftClickExecute(mc.gameSettings.keyBindAttack.getKeyCode());
		}
	}

	private void leftClickExecute(int key) {
		if (breakBlock()) return;

		long currentTime = System.currentTimeMillis();

		if (leftUpTime > 0L && leftDownTime > 0L) {
			if (currentTime > leftUpTime && leftDown) {
				click(true);
				genLeftTimings();
				leftDown = false;
			} else if (currentTime > leftDownTime) {
				click(false);
				leftDown = true;
			}
		} else {
			genLeftTimings();
		}
	}

	private void genLeftTimings() {
		double cps = ranModuleVal(minCPS, maxCPS, random) + 0.4 * random.nextDouble();
		long delay = (long) (1000.0 / cps);

		long currentTime = System.currentTimeMillis();

		if (currentTime > leftk) {
			leftn = random.nextInt(100) >= 85;
			if (leftn) leftm = 1.1 + random.nextDouble() * 0.15;
			leftk = currentTime + 500 + random.nextInt(1500);
		}

		if (leftn) delay *= leftm;

		if (currentTime > leftl) {
			if (random.nextInt(100) >= 80) delay += 50 + random.nextInt(100);
			leftl = currentTime + 500 + random.nextInt(1500);
		}

		leftUpTime = currentTime + delay;
		leftDownTime = currentTime + delay / 2 - random.nextInt(10);
	}

	private void click(boolean press) {
		int key = mc.gameSettings.keyBindAttack.getKeyCode();
		KeyBinding.setKeyBindState(key, press);
		if (press) KeyBinding.onTick(key);
		PlayerUtil.setMouseButtonState(0, press);
	}

	private boolean breakBlock() {
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			if (conditionals.isEnabled("BreakBlocks")) return true;
			ReflectUtil.setCurBlockDamage(0);
		}
		return false;
	}

	private double ranModuleVal(SliderValue min, SliderValue max, Random r) {
		return min.getValue() + r.nextDouble() * (max.getValue() - min.getValue());
	}

	private int getRandomizedCPS() {
		int base = 25, delta = 2;
		return ThreadLocalRandom.current().nextInt(Math.max(1, base - delta), base + delta + 1);
	}
}
