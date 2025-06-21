package cc.unknown.module.impl.visual;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.Bool;

@ModuleInfo(name = "NoRender", description = "...", category = Category.VISUAL)
public class NoRender extends Module {
	
	public final Bool boss = new Bool("BossBar", this, false);
	public final Bool pumpkin = new Bool("PumpkinOverlay", this, true);
	public final Bool portal = new Bool("Portal", this, true);
	public final Bool vignette = new Bool("Vignette", this, false);
}