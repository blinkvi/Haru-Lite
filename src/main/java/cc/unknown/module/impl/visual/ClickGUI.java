package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.enums.StickersType;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.ColorValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;

@ModuleInfo(name = "ClickGUI", description = "Provides a graphical user interface (GUI).", category = Category.VISUAL, key = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
	
	public final ModeValue waifuType = new ModeValue("Waifu", this, StickersType.NONE, StickersType.values());
	
	public final SliderValue width = new SliderValue("Width", this, 100, 10, 1000, 10, () -> !waifuType.is("None"));
	public final SliderValue height = new SliderValue("Height", this, 10, 10, 1000, 10, () -> !waifuType.is("None"));
	
	public final MultiBoolValue pref = new MultiBoolValue("Preferences", this, Arrays.asList(
			new BoolValue("HideElementsInGui", true),
			new BoolValue("RoundedOutline", false),
			new BoolValue("RoundedButtons", false),
			new BoolValue("Shaders", false),
			new BoolValue("ToolTips", true)));
	
	public final SliderValue alpha = new SliderValue("AlphaBackground", this, 120, 20, 190, 10);
	
    public final ColorValue outlineColor = new ColorValue("OutlineColor", this, new Color(255, 0, 0));
    public final ColorValue mainColor = new ColorValue("MainColor", this, new Color(255, 0, 0));
    
    @Override
    public void onEnable() {
    	mc.displayGuiScreen(Haru.instance.getDropGui());
        toggle();
        super.onEnable();
    }
}