package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.render.enums.StickersType;
import cc.unknown.util.render.font.FontRenderer;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

@ModuleInfo(name = "Interface", description = "Renders the client interface.", category = Category.VISUAL)
public class Interface extends Module {
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
	
	public final ModeValue stickersType = new ModeValue("Sticker", this, StickersType.UZAKI, StickersType.values());
	public final ModeValue color = new ModeValue("ArraylistColor", this, () -> elements.isEnabled("ArrayList"), "Slinky", "Rainbow", "Fade", "Slinky", "Magic", "Neon", "Astolfo", "Blaze", "Ghoul", "Custom");
	public final ColorValue mainColor = new ColorValue("MainColor", this, new Color(128, 128, 255), () -> color.is("Custom") || color.is("Fade"));
	public final ColorValue secondColor = new ColorValue("SecondColor", this, new Color(128, 255, 255), () -> color.is("Fade"));
	public final SliderValue fadeSpeed = new SliderValue("FadeSpeed", this, 1, 1, 10, 1, () -> color.is("Fade"));
	public final BoolValue shaders = new BoolValue("Shaders", this, true, () -> elements.isEnabled("ArrayList"));
	
	public final SliderValue fontSize = new SliderValue("FontSize", this, 16, 6, 20, () -> elements.isEnabled("ArrayList"));
	public final SliderValue textHeight = new SliderValue("FontHeight", this, 3, 0, 15, () -> elements.isEnabled("ArrayList"));
	
	public final BoolValue background = new BoolValue("Background", this, true, () -> elements.isEnabled("ArrayList"));
	private final ColorValue backgroundColor = new ColorValue("Color", this, new Color(32, 32, 64), background::get);
	private final SliderValue backgroundAlpha = new SliderValue("BackgroundAlpha", this, 100, 1, 255, background::get);
	
    public final BoolValue hideScoreRed = new BoolValue("HideRedPoints", this, true, () -> elements.isEnabled("Scoreboard"));
    public final BoolValue fixHeight = new BoolValue("FixHeight", this, true, () -> elements.isEnabled("Scoreboard"));
    public final BoolValue hideBackground = new BoolValue("HideBackground", this, true, () -> elements.isEnabled("Scoreboard"));
    public final BoolValue antiStrike = new BoolValue("AntiStrike", this, true, () -> elements.isEnabled("Scoreboard"));
    
	public final MultiBoolValue hideCategory = new MultiBoolValue("HideCategory", this, () -> elements.isEnabled("ArrayList"), Arrays.asList(
			new BoolValue("Combat", false),
			new BoolValue("Move", false),
			new BoolValue("Utility", false),
			new BoolValue("Visual", true)));
    
    public int scoreBoardHeight = 0;
    private final Pattern LINK_PATTERN = Pattern.compile("(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[A-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)");

    
	public int getRainbow(int counter) {
		return Color.HSBtoRGB(getRainbowHSB(counter)[0], getRainbowHSB(counter)[1], getRainbowHSB(counter)[2]);
	}

	public float[] getRainbowHSB(int counter) {
		double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * 20) / 8;
		rainbowState %= 360;

		float hue = (float) (rainbowState / 360);
		float saturation = mainColor.getSaturation();
		float brightness = mainColor.getBrightness();

		return new float[] { hue, saturation, brightness };
	}

	public int color() {
		return color(0);
	}

	public int color(int counter, int alpha) {
		int col = mainColor.get().getRGB();
		switch (color.get()) {
		case "Slinky":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.PINK, new Color(255, 0, 255), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Magic":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.CYAN, new Color(142, 45, 226), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Neon":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(Color.MAGENTA, new Color(0, 200, 255), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Astolfo":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(64, 224, 208), new Color(152, 165, 243), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Blaze":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(139, 0, 0), new Color(255, 140, 0), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Ghoul":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(new Color(255, 0, 0), new Color(0, 0, 0), 1000.0F, counter, 70L, 1).getRGB(), alpha);
			break;
		case "Rainbow":
			col = ColorUtil.swapAlpha(getRainbow(counter), alpha);
			break;
		case "Fade":
			col = ColorUtil.swapAlpha(ColorUtil.colorSwitch(mainColor.get(), secondColor.get(), 2000.0F, counter, 75L, fadeSpeed.get()).getRGB(), alpha);
			break;
		case "Custom":
			col = ColorUtil.swapAlpha(mainColor.get().getRGB(), alpha);
			break;
		default:
			break;
		}
		return new Color(col, true).getRGB();
	}

	public FontRenderer getFont() {
		return FontUtil.getFontRenderer("consolas.ttf", (int) fontSize.get());
	}

	public int backgroundColor(int counter, int alpha) {
		return ColorUtil.swapAlpha(backgroundColor.get().getRGB(), alpha);
	}

	public int color(int counter) {
		return color(counter, mainColor.get().getAlpha());
	}

	public int backgroundColor(int counter) {
		return backgroundColor(counter, (int) backgroundAlpha.get());
	}

	public int backgroundColor() {
		return backgroundColor(0);
	}
	
	public void drawScoreboard(ScaledResolution scaledRes, ScoreObjective objective, Scoreboard scoreboard, Collection<Score> scores) {
        List<Score> list = Lists.newArrayList(Iterables.filter(scores, p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));

        Scoreboard scoreboard1 = objective.getScoreboard();
        Collection<Score> collection = scoreboard1.getSortedScores(objective);
        
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        }
        else {
            collection = list;
        }

        int i = mc.fontRendererObj.getStringWidth(objective.getDisplayName());

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard1.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            i = Math.max(i, mc.fontRendererObj.getStringWidth(s));
        }

        int i1 = collection.size() * mc.fontRendererObj.FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i - k1;
        int j = 0;

        if (this.fixHeight.get()) {
            j1 = Math.max(j1, scoreBoardHeight + i1 + mc.fontRendererObj.FONT_HEIGHT + 17);
        }

        for (Score score1 : collection) {
            ++j;
            ScorePlayerTeam scoreplayerteam1 = scoreboard1.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
            int k = j1 - j * mc.fontRendererObj.FONT_HEIGHT;
            int l = scaledRes.getScaledWidth() - k1 + 2;
            if(hideBackground.get()) {
            	Gui.drawRect(l1 - 2, k, l, k, 1342177280);
            } else {
            	Gui.drawRect(l1 - 2, k, l, k + mc.fontRendererObj.FONT_HEIGHT, 1342177280);
            }
            
            final Matcher linkMatcher = LINK_PATTERN.matcher(s1);
            if(antiStrike.get() && linkMatcher.find()) {
                s1 = "";
            }
            
            mc.fontRendererObj.drawString(s1, l1, k, 553648127);
            
            if (!hideScoreRed.get())
            	mc.fontRendererObj.drawString(s2, l - mc.fontRendererObj.getStringWidth(s2), k, 553648127);

            if (j == collection.size()) {
                String s3 = objective.getDisplayName();
                if(!hideBackground.get()) {
	                Gui.drawRect(l1 - 2, k - mc.fontRendererObj.FONT_HEIGHT - 1, l, k - 1, 1610612736);
	                Gui.drawRect(l1 - 2, k - 1, l, k, 1342177280);
                }
                
                mc.fontRendererObj.drawString(s3, l1 + i / 2 - mc.fontRendererObj.getStringWidth(s3) / 2, k - mc.fontRendererObj.FONT_HEIGHT, 553648127);
            }
        }
    }
}