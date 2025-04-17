package cc.unknown.handlers;

import java.util.Arrays;
import java.util.List;

import cc.unknown.event.PreTickEvent;
import cc.unknown.module.Module;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.Accessor;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class GuiMoveHandler implements Accessor {
	private final List<KeyBinding> moveKeys = Arrays.asList(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
			mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump,
			mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindSneak);

	@SubscribeEvent
	public void onPreTick(PreTickEvent event) {
		if (mc.currentScreen instanceof DropGui) {
			for (KeyBinding bind : moveKeys) {
				int keyCode = bind.getKeyCode();
				boolean isPressed = GameSettings.isKeyDown(bind);
				KeyBinding.setKeyBindState(keyCode, isPressed);
			}
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (event.phase == Phase.END) {
			for (Module module : getModuleManager().getModules()) {
				if (mc.currentScreen instanceof DropGui) {
					module.guiUpdate();
				}

				if (module.isEnabled()) {
					module.onUpdate();
				}
			}
		}
	}
}
