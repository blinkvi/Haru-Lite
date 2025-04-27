package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.GameEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;

@ModuleInfo(name = "FullBright", description = "Increases the brightness of the game world.", category = Category.VISUAL)
public class FullBright extends Module {

	private float oldGamma;
	
    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
    }

    @EventLink
    public final Listener<GameEvent> onGame = event -> mc.gameSettings.gammaSetting = 100.0f;
}