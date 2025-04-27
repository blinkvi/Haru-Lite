package cc.unknown.handlers;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.KeyInputEvent;
import cc.unknown.event.impl.MouseEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.utility.FreeLook;
import cc.unknown.util.Managers;

public class KeyHandler implements Managers {
	
	@EventLink
	public final Listener<KeyInputEvent> onKeyInput = event -> {
		int key = Keyboard.getEventKey();

		if (key != Keyboard.CHAR_NONE && Keyboard.getEventKeyState()) {
			Haru.modMngr.getModules().stream()
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
			Haru.modMngr.getModules().stream()
				.filter(module -> module instanceof FreeLook && module.getKeyBind() == key && module.isEnabled())
				.findFirst()
				.ifPresent(Module::toggle);
		}
	};
	
	@EventLink
	public final Listener<MouseEvent> onMouse = event -> {
		if (event.button == 0) {
	        if (mc.gameSettings.keyBindTogglePerspective.isPressed()) {
	            mc.gameSettings.thirdPersonView = (mc.gameSettings.thirdPersonView + 1) % 3;
	            mc.renderGlobal.setDisplayListEntitiesDirty();
	        }
		}
	};
}
