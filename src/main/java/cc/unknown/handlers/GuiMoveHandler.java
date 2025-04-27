package cc.unknown.handlers;

import java.util.Arrays;
import java.util.List;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiMoveHandler implements Accessor {
	private final List<KeyBinding> moveKeys = Arrays.asList(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
			mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump,
			mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindSneak);

    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
		if (!PlayerUtil.isInGame()) return;

		if (mc.currentScreen instanceof DropGui) {
			moveKeys.stream()
				.forEach(bind -> {
					int keyCode = bind.getKeyCode();
					boolean isPressed = GameSettings.isKeyDown(bind);
					KeyBinding.setKeyBindState(keyCode, isPressed);
				});
		}
	};
}
