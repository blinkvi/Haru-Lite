package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.enums.StickersType;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Palette;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;

@ModuleInfo(name = "ClickGUI", description = "Provides a graphical user interface (GUI).", category = Category.VISUAL, key = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
	
	public final Mode waifuType = new Mode("Waifu", this, StickersType.NONE, StickersType.values());
	
	public final Slider width = new Slider("Width", this, 100, 10, 1000, 10, () -> !waifuType.is("None"));
	public final Slider height = new Slider("Height", this, 10, 10, 1000, 10, () -> !waifuType.is("None"));
	
	public final MultiBool pref = new MultiBool("Preferences", this, Arrays.asList(
			new Bool("HideElementsInGui", true),
			new Bool("RoundedOutline", true),
			new Bool("RoundedButtons", true),
			new Bool("Shaders", true),
			new Bool("ToolTips", false)));
	
	public final Slider alpha = new Slider("AlphaBackground", this, 120, 20, 190, 10);
	
    public final Palette outlineColor = new Palette("OutlineColor", this, new Color(255, 0, 0));
    public final Palette mainColor = new Palette("MainColor", this, new Color(255, 0, 0));
    
    @Override
    public void onEnable() {
    	mc.displayGuiScreen(Haru.instance.getDropGui());
        toggle();
        super.onEnable();
    }
}