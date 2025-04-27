package cc.unknown.module.impl.move;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.client.settings.KeyBinding;

@ModuleInfo(name = "Sprint", description = "Enables automatic sprinting while moving in the game.", category = Category.MOVE)
public class Sprint extends Module {
	
    @EventLink
    public final Listener<StrafeEvent> onStrafe = event -> {
		if (!PlayerUtil.isInGame()) return;

		if (mc.inGameHasFocus) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
		}
    };
}