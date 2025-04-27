package cc.unknown.module.impl.move;

import cc.unknown.event.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Sprint", description = "Enables automatic sprinting while moving in the game.", category = Category.MOVE)
public class Sprint extends Module {

	@SubscribeEvent
	public void onStrafe(StrafeEvent event) {
		if (isInGame() && mc.inGameHasFocus) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
		}
	}
}