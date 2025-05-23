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
import cc.unknown.value.impl.Palette;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;

@ModuleInfo(name = "Interface", description = "Renders the client interface.", category = Category.VISUAL)
public class Interface extends Module {
	public final MultiBool hideCategory = new MultiBool("HideCategory", this, () -> this.elements.isEnabled("ArrayList"), Arrays.asList(
			new Bool("Combat", false),
			new Bool("Move", false),
			new Bool("Utility", false),
			new Bool("Visual", true)));
	
	public final MultiBool elements = new MultiBool("Elements", this, Arrays.asList(
			new Bool("Watermark", true),
			new Bool("Scoreboard", false),
			new Bool("Stickers", false),
			new Bool("IGN", false),
			new Bool("FPS", false),
			new Bool("Ping", false),
			new Bool("CPS", true),
			new Bool("PlayerPosition", false),
			new Bool("ArrayList", true), 
			new Bool("PotionStatus", false), 
			new Bool("Inventory", false)));
	
	public final Mode stickersType = new Mode("Sticker", this, () -> this.elements.isEnabled("Stickers"), StickersType.UZAKI, StickersType.values());
	public final Mode color = new Mode("ArraylistColor", this, () -> this.elements.isEnabled("ArrayList"), "Neon", "Fade", "Slinky", "Magic", "Neon", "Blaze", "Ghoul", "Static");
	public final Palette mainColor = new Palette("MainColor", this, new Color(128, 128, 255), () -> color.is("Static") || color.is("Fade"));
	public final Palette secondColor = new Palette("SecondColor", this, new Color(128, 255, 255), () -> color.is("Fade"));
	public final Slider fadeSpeed = new Slider("FadeSpeed", this, 1, 1, 10, 1, () -> color.is("Fade"));
	
	public final Slider fontSize = new Slider("FontSize", this, 17, 6, 20, () -> this.elements.isEnabled("ArrayList"));
	public final Slider textHeight = new Slider("FontHeight", this, 3, 0, 15, () -> this.elements.isEnabled("ArrayList"));
	
	public final Bool background = new Bool("Background", this, true, () -> this.elements.isEnabled("ArrayList"));
	private final Palette backgroundColor = new Palette("Color", this, new Color(0, 0, 0), background::get);
	private final Slider backgroundAlpha = new Slider("BackgroundAlpha", this, 100, 1, 255, background::get);
		
	public final Bool noRenderScoreboard = new Bool("NoRenderScoreboard", this, false, () -> this.elements.isEnabled("Scoreboard"));
    public final Bool hideScoreRed = new Bool("HideRedPoints", this, true, () -> this.elements.isEnabled("Scoreboard"));
    public final Bool fixHeight = new Bool("FixHeight", this, true, () -> this.elements.isEnabled("Scoreboard"));
    public final Bool hideBackground = new Bool("HideBackground", this, true, () -> this.elements.isEnabled("Scoreboard"));
    public final Bool antiStrike = new Bool("AntiStrike", this, true, () -> this.elements.isEnabled("Scoreboard"));
    
	private final Map<String, BiFunction<Integer, Integer, Integer>> COLOR_MODES = new HashMap<>();

	{
	    COLOR_MODES.put("Slinky",  (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(Color.PINK, new Color(255, 0, 255), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Magic",   (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(Color.CYAN, new Color(142, 45, 226), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Neon",    (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(Color.MAGENTA, new Color(0, 200, 255), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Blaze",   (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(new Color(139, 0, 0), new Color(255, 140, 0), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Ghoul",   (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(new Color(255, 0, 0), new Color(0, 0, 0), 1000F, counter, 70L, 1), alpha));
	    COLOR_MODES.put("Fade",    (counter, alpha) -> alphaColor(ColorUtil.colorSwitch(mainColor.get(), secondColor.get(), 1000F, counter, 70L, fadeSpeed.get()), alpha));
	    COLOR_MODES.put("Static",  (counter, alpha) -> alphaColor(mainColor.get(), alpha));
	}

	private int alphaColor(Color color, int alpha) {
	    return new Color(ColorUtil.swapAlpha(color.getRGB(), alpha), true).getRGB();
	}

	public int color(int counter, int alpha) {
	    BiFunction<Integer, Integer, Integer> modeFunction = COLOR_MODES.get(color.get());
	    return modeFunction != null ? modeFunction.apply(counter, alpha) : alphaColor(mainColor.get(), alpha);
	}

	public int color(int counter) {
	    return color(counter, mainColor.get().getAlpha());
	}

	public int color() {
	    return color(0);
	}

	public int backgroundColor(int counter, int alpha) {
	    return alphaColor(backgroundColor.get(), alpha);
	}

	public int backgroundColor(int counter) {
	    return backgroundColor(counter, (int) backgroundAlpha.get());
	}

	public int backgroundColor() {
	    return backgroundColor(0);
	}

}