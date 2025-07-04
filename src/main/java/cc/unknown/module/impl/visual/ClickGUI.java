package cc.unknown.module.impl.visual;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiSlider;
import cc.unknown.value.impl.Slider;

@ModuleInfo(name = "ClickGUI", description = "Provides a graphical user interface (GUI).", category = Category.VISUAL, key = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
	
	public final Bool hideElements = new Bool("HideElementsInGui", this, false);
	public final Slider alpha = new Slider("AlphaBackground", this, 120, 20, 190, 10);
		
	public final Bool shaders = new Bool("Shaders", this, true);
	public final Bool roundedOutline = new Bool("RoundedOutline", this, true, shaders::get);
	public final Bool roundedButtons = new Bool("RoundedButtons", this, true, shaders::get);
	
	public final MultiSlider colorMain = new MultiSlider("MainColor", this, Arrays.asList(
			new Slider("Red", 255, 0, 255),
			new Slider("Green", 0, 0, 255),
			new Slider("Blue", 0, 0, 255)));
	
	public final MultiSlider colorOutline = new MultiSlider("OutlineColor", this, Arrays.asList(
			new Slider("Red", 255, 0, 255),
			new Slider("Green", 0, 0, 255),
			new Slider("Blue", 0, 0, 255)));

    @Override
    public void onEnable() {
    	mc.displayGuiScreen(Haru.instance.getDropGui());
        toggle();
        super.onEnable();
    }
}