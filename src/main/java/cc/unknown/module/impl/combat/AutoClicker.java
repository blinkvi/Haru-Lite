package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.Random;

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
	private final SliderValue minCPS = new SliderValue("MinCPS", this, 9, 1, 25, 1);
	private final SliderValue maxCPS = new SliderValue("MaxCPS", this, 13, 1, 25, 1);
	private final ModeValue randomize = new ModeValue("Randomize", this, "ButterFly", "ButterFly", "Jitter", "Drag");

	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("Inventory", false),
			new BoolValue("OnlyWeapon", false),
			new BoolValue("BreakBlocks", false)));
	
	private long lastClick;
	private long nextReleaseTime;
	private boolean leftDown;
	private long leftDownTime = 0L;
	private long leftUpTime = 0L;
	private long leftk;
	private long leftl;
	private double leftm;
	private boolean leftn;

	private Random rand = new Random();

	@Override
	public void onEnable() {
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
	public void onRender3D(RenderWorldLastEvent event) {
		if (mc.currentScreen != null) return;
		
		if (!randomize.is("ButterFly")) return;
		
		if (mode.is("Legit")) {
			ravenClick();
		} else if (mode.is("Blatant")) {
			skidClick(null, null, event);
		}
	}
	
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (mc.currentScreen != null) return;
		
		if (!randomize.is("Drag")) return;

		if (mode.is("Legit")) {
			ravenClick();
		} else if (mode.is("Blatant")) {
			skidClick(event, null, null);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (mc.currentScreen != null) return;
		
		if (!randomize.is("Jitter")) return;

		if (mode.is("Legit")) {
			ravenClick();
		} else if (mode.is("Blatant")) {
			skidClick(null, event, null);
		}
	}

	@SubscribeEvent
	public void onPreAttack(PrePositionEvent event) {
		if (conditionals.isEnabled("Inventory") && mc.currentScreen instanceof GuiContainer) {
			InventoryUtil.guiClicker(mc.currentScreen, 0, getRandomizedCPS());
		}
	}

	private void skidClick(TickEvent.RenderTickEvent renderTick, TickEvent.PlayerTickEvent playerTick, RenderWorldLastEvent event) {
	    if (!isInGame()) return;

	    double clickInterval = 1.0 / MathUtil.randomDouble(minCPS.getValue() - 0.2D, maxCPS.getValue());
	    double holdDuration = clickInterval / MathUtil.randomDouble(minCPS.getValue() - 0.02D, maxCPS.getValue());

	    Mouse.poll();

	    if (Mouse.isButtonDown(0)) {
	        if (breakBlock()) return;

	        if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) return;

	        double currentInterval = 1.0 / MathUtil.randomDouble(minCPS.getValue() - 0.2D, maxCPS.getValue());
	        long currentTime = System.currentTimeMillis();

	        if (currentTime - lastClick > currentInterval * 1000) {
	            lastClick = currentTime;
	            if (nextReleaseTime < lastClick) {
	                nextReleaseTime = lastClick;
	            }

	            int attackKey = mc.gameSettings.keyBindAttack.getKeyCode();
	            KeyBinding.setKeyBindState(attackKey, true);
	            KeyBinding.onTick(attackKey);
	            PlayerUtil.setMouseButtonState(0, true);
	        } else if (currentTime - nextReleaseTime > holdDuration * 1000) {
	            int attackKey = mc.gameSettings.keyBindAttack.getKeyCode();
	            KeyBinding.setKeyBindState(attackKey, false);
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
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (conditionals.isEnabled("BreakBlocks")) {
                return true;
            } else {
            	ReflectUtil.setCurBlockDamage(0);
            	return false;
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