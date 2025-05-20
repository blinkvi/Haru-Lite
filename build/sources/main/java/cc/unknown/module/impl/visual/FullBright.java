package cc.unknown.module.impl.visual;

import cc.unknown.event.GameLoopEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    @SubscribeEvent
    public void onGame(GameLoopEvent event) {
    	mc.gameSettings.gammaSetting = 100.0f;
    }
}