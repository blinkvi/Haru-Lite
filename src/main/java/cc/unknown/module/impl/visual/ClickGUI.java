package cc.unknown.module.impl.visual;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.enums.StickersType;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.Slider;

@ModuleInfo(name = "ClickGUI", description = "Provides a graphical user interface (GUI).", category = Category.VISUAL, key = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
	
	
	public final Mode waifuType = new Mode("Waifu", this, StickersType.NONE, StickersType.values());
	
	public final Slider width = new Slider("Width", this, 100, 10, 1000, 10, () -> !waifuType.is("None"));
	public final Slider height = new Slider("Height", this, 10, 10, 1000, 10, () -> !waifuType.is("None"));
	
	public final Bool toolTips = new Bool("ToolTips", this, false);
	public final Bool hideElements = new Bool("HideElementsInGui", this, true);
	public final Slider alpha = new Slider("AlphaBackground", this, 120, 20, 190, 10);
		
	public final Bool shaders = new Bool("Shaders", this, false);
	public final Bool roundedOutline = new Bool("RoundedOutline", this, false, shaders::get);
	public final Bool roundedButtons = new Bool("RoundedButtons", this, false, shaders::get);
	
	public final Slider red = new Slider("Red [Outline]", this, 255, 0, 255, 1);
	public final Slider green = new Slider("Green [Outline]", this, 0, 0, 255, 1);
	public final Slider blue = new Slider("Blue [Outline]", this, 0, 0, 255, 1);
	
	public final Slider red2 = new Slider("Red [Main]", this, 255, 0, 255, 1);
	public final Slider green2 = new Slider("Green [Main]", this, 0, 0, 255, 1);
	public final Slider blue2 = new Slider("Blue [Main]", this, 0, 0, 255, 1);

    @Override
    public void onEnable() {
    	mc.displayGuiScreen(Haru.instance.getDropGui());
        toggle();
        super.onEnable();
    }
}