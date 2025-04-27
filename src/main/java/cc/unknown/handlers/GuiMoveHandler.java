package cc.unknown.handlers;

import java.util.Arrays;
import java.util.List;

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
}
