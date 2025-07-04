package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.render.enums.StickersType;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.MultiSlider;
import cc.unknown.value.impl.Slider;

@ModuleInfo(name = "Interface", description = "Renders the client interface.", category = Category.VISUAL)
public class Interface extends Module {
	public final Mode stickersType = new Mode("Sticker", this, StickersType.LOONA, StickersType.values());
	public final Mode mode = new Mode("Mode", this, "Neon", "Fade", "Slinky", "Magic", "Neon", "Blaze", "Ghoul", "Static");

	public final MultiSlider firstColor = new MultiSlider("FirstColor", this, () -> mode.is("Static") || mode.is("Fade"), Arrays.asList(
			new Slider("Red", 255, 0, 255),
			new Slider("Green", 0, 0, 255),
			new Slider("Blue", 0, 0, 255)));
	
	public final MultiSlider twoColor = new MultiSlider("SecondColor", this, () -> mode.is("Fade"), Arrays.asList(
			new Slider("Red", 255, 0, 255),
			new Slider("Green", 0, 0, 255),
			new Slider("Blue", 0, 0, 255)));
	
	public final Slider fadeSpeed = new Slider("FadeSpeed", this, 1, 1, 10, () -> mode.is("Fade"));
	
	public final Slider fontSize = new Slider("FontSize", this, 17, 6, 49);
	public final Slider textHeight = new Slider("FontHeight", this, 3, 0, 40);
	
	public final Bool background = new Bool("Background", this, true);
	public final Slider backgroundAlpha = new Slider("BackgroundAlpha", this, 100, 1, 255, background::get);
    
	public final MultiBool hideCategory = new MultiBool("HideCategory", this, Arrays.asList(
			new Bool("Combat", false),
			new Bool("Move", false),
			new Bool("Utility", false),
			new Bool("Visual", true)));
    
	public final MultiBool elements = new MultiBool("Elements", this, Arrays.asList(
			new Bool("Watermark", true),
			new Bool("ModuleList", false),
			new Bool("IGN", false),
			new Bool("FPS", false),
			new Bool("Ping", false),
			new Bool("CPS", true),
			new Bool("PlayerPosition", false),
			new Bool("PotionStatus", false), 
			new Bool("Inventory", false)));

	private final Map<String, BiFunction<Integer, Integer, Integer>> COLOR_MODES = new HashMap<>();

	{
		
	    COLOR_MODES.put("Slinky",  (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(Color.PINK, new Color(255, 0, 255), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Magic",   (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(Color.CYAN, new Color(142, 45, 226), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Neon",    (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(Color.MAGENTA, new Color(0, 200, 255), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Blaze",   (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(new Color(139, 0, 0), new Color(255, 140, 0), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Ghoul",   (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(new Color(255, 0, 0), new Color(0, 0, 0), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Fade",    (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(new Color(firstColor.getAsInt(0), firstColor.getAsInt(1), firstColor.getAsInt(2)), new Color(twoColor.getAsInt(0), twoColor.getAsInt(1), twoColor.getAsInt(2)), 1000F, counter, 70L, fadeSpeed.get()), alpha));
	    COLOR_MODES.put("Static",  (counter, alpha) -> alphaColor(new Color(firstColor.getAsInt(0), firstColor.getAsInt(1), firstColor.getAsInt(2)), alpha));
	}

	private int alphaColor(Color color, int alpha) {
	    return new Color(ColorUtil.swapAlpha(color.getRGB(), alpha), true).getRGB();
	}

	public int color(int counter, int alpha) {
	    BiFunction<Integer, Integer, Integer> modeFunction = COLOR_MODES.get(mode.get());
	    return modeFunction != null ? modeFunction.apply(counter, alpha) : alphaColor(
	    		new Color(firstColor.getAsInt(0), firstColor.getAsInt(1), firstColor.getAsInt(2)), alpha);
	}

	public int color(int counter) {
	    return color(counter, new Color(firstColor.getAsInt(0), firstColor.getAsInt(1), firstColor.getAsInt(2)).getAlpha());
	}

	public int color() {
	    return color(0);
	}
	
	public int backgroundColor() {
		return new Color(0, 0, 0, backgroundAlpha.getAsInt()).getRGB();
	}
}