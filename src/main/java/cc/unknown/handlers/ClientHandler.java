package cc.unknown.handlers;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.impl.utility.FreeLook;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.structure.list.SList;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientHandler implements Accessor {
	private final List<KeyBinding> moveKeys = Arrays.asList(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak);
	private static final SList<Long> leftClicks = new SList<>();
	private static final SList<Long> rightClicks = new SList<>();
	
	public static long leftClickTimer = 0L;
	public static long rightClickTimer = 0L;
	
	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
		if (!isInGame()) return;

		if (event.phase == Phase.START && mc.currentScreen instanceof DropGui) {
			moveKeys.stream()
				.forEach(bind -> {
					int keyCode = bind.getKeyCode();
					boolean isPressed = GameSettings.isKeyDown(bind);
					KeyBinding.setKeyBindState(keyCode, isPressed);
				});
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		int key = Keyboard.getEventKey();

		if (key != Keyboard.CHAR_NONE && Keyboard.getEventKeyState()) {
			Haru.instance.getModuleManager().getModules().stream()
				.filter(module -> module.getKeyBind() == key)
				.findFirst()
				.ifPresent(module -> {
					if (module instanceof FreeLook) {
						if (!module.isEnabled()) {
							module.toggle();
						}
					} else {
						if (!Keyboard.isRepeatEvent()) {
							module.toggle();
						}
					}
				});
		} else if (key != Keyboard.CHAR_NONE && !Keyboard.getEventKeyState()) {
			Haru.instance.getModuleManager().getModules().stream()
				.filter(module -> module instanceof FreeLook && module.getKeyBind() == key && module.isEnabled())
				.findFirst()
				.ifPresent(Module::toggle);
		}
	}
	
	@SubscribeEvent
	public void onClick(MouseEvent event) {
		if (event.button == 0) {
	        if (mc.gameSettings.keyBindTogglePerspective.isPressed()) {
	            mc.gameSettings.thirdPersonView = (mc.gameSettings.thirdPersonView + 1) % 3;
	            mc.renderGlobal.setDisplayListEntitiesDirty();
	        }
		}
		
		if (event.buttonstate) {
			if (event.button == 0 && !mc.thePlayer.isBlocking()) {
				addLeftClick();
			} else if (event.button == 1 && (InventoryUtil.getAnyBlock() | InventoryUtil.getProjectiles())) {
				addRightClick();
			}
		}
	}
	
	public static void addLeftClick() {
		leftClicks.add(leftClickTimer = System.currentTimeMillis());
	}

	public static void addRightClick() {
		rightClicks.add(rightClickTimer = System.currentTimeMillis());
	}

	public static int getLeftClickCounter() {
	    if (mc.thePlayer == null || mc.theWorld == null) return leftClicks.size();
	    leftClicks.removeIf(lon -> lon < System.currentTimeMillis() - 1000L);
	    return leftClicks.size();
	}

	public static int getRightClickCounter() {
	    if (mc.thePlayer == null || mc.theWorld == null) return rightClicks.size();
	    rightClicks.removeIf(lon -> lon < System.currentTimeMillis() - 1000L);
	    return rightClicks.size();
	}
}
