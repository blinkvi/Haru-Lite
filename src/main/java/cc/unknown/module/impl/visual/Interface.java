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
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.ColorValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;

@ModuleInfo(name = "Interface", description = "Renders the client interface.", category = Category.VISUAL)
public class Interface extends Module {
	public final MultiBoolValue hideCategory = new MultiBoolValue("HideCategory", this, () -> this.elements.isEnabled("ArrayList"), Arrays.asList(
			new BoolValue("Combat", false),
			new BoolValue("Move", false),
			new BoolValue("Utility", false),
			new BoolValue("Visual", true)));
	
	public final ModeValue stickersType = new ModeValue("Sticker", this, () -> this.elements.isEnabled("Stickers"), StickersType.UZAKI, StickersType.values());
	public final ModeValue color = new ModeValue("ArraylistColor", this, () -> this.elements.isEnabled("ArrayList"), "Neon", "Fade", "Slinky", "Magic", "Neon", "Blaze", "Ghoul", "Static");
	public final ColorValue mainColor = new ColorValue("MainColor", this, new Color(128, 128, 255), () -> color.is("Static") || color.is("Fade"));
	public final ColorValue secondColor = new ColorValue("SecondColor", this, new Color(128, 255, 255), () -> color.is("Fade"));
	public final SliderValue fadeSpeed = new SliderValue("FadeSpeed", this, 1, 1, 10, 1, () -> color.is("Fade"));
	public final BoolValue shaders = new BoolValue("Shaders", this, true);
	
	public final SliderValue fontSize = new SliderValue("FontSize", this, 16, 6, 20, () -> this.elements.isEnabled("ArrayList"));
	public final SliderValue textHeight = new SliderValue("FontHeight", this, 3, 0, 15, () -> this.elements.isEnabled("ArrayList"));
	
	public final BoolValue background = new BoolValue("Background", this, true, () -> this.elements.isEnabled("ArrayList"));
	private final ColorValue backgroundColor = new ColorValue("Color", this, new Color(0, 0, 0), background::get);
	private final SliderValue backgroundAlpha = new SliderValue("BackgroundAlpha", this, 100, 1, 255, background::get);
		
	public final BoolValue noRenderScoreboard = new BoolValue("NoRenderScoreboard", this, false, () -> this.elements.isEnabled("Scoreboard"));
    public final BoolValue hideScoreRed = new BoolValue("HideRedPoints", this, true, () -> this.elements.isEnabled("Scoreboard"));
    public final BoolValue fixHeight = new BoolValue("FixHeight", this, true, () -> this.elements.isEnabled("Scoreboard"));
    public final BoolValue hideBackground = new BoolValue("HideBackground", this, true, () -> this.elements.isEnabled("Scoreboard"));
    public final BoolValue antiStrike = new BoolValue("AntiStrike", this, true, () -> this.elements.isEnabled("Scoreboard"));

	public final MultiBoolValue elements = new MultiBoolValue("Elements", this, Arrays.asList(
			new BoolValue("Watermark", true),
			new BoolValue("Scoreboard", false),
			new BoolValue("Stickers", false),
			new BoolValue("IGN", false),
			new BoolValue("FPS", false),
			new BoolValue("Ping", false),
			new BoolValue("CPS", true),
			new BoolValue("PlayerPosition", false),
			new BoolValue("ArrayList", true), 
			new BoolValue("PotionStatus", false), 
			new BoolValue("Inventory", false)));
    
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