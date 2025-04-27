package cc.unknown.handlers;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.impl.utility.FreeLook;
import cc.unknown.util.Accessor;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandler implements Accessor {
	
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
	}
}
