package cc.unknown.util.render.client;

import static org.lwjgl.opengl.GL11.glColor4ub;

import java.awt.Color;

import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ColorUtil implements Accessor {
	
	public static EnumChatFormatting yellow = EnumChatFormatting.YELLOW;
	public static EnumChatFormatting red = EnumChatFormatting.RED;
	public static EnumChatFormatting reset = EnumChatFormatting.RESET;
	public static EnumChatFormatting white = EnumChatFormatting.RESET;
	public static EnumChatFormatting aqua = EnumChatFormatting.AQUA;
	public static EnumChatFormatting gray = EnumChatFormatting.GRAY;
	public static EnumChatFormatting green = EnumChatFormatting.GREEN;
	public static EnumChatFormatting blue = EnumChatFormatting.BLUE;
	public static EnumChatFormatting black = EnumChatFormatting.BLACK;
	public static EnumChatFormatting gold = EnumChatFormatting.GOLD;
	
	public static EnumChatFormatting darkAqua = EnumChatFormatting.DARK_AQUA;
	public static EnumChatFormatting darkGray = EnumChatFormatting.DARK_GRAY;
	public static EnumChatFormatting darkPurple = EnumChatFormatting.DARK_PURPLE;
	public static EnumChatFormatting darkBlue = EnumChatFormatting.DARK_BLUE;
	public static EnumChatFormatting darkGreen = EnumChatFormatting.DARK_GREEN;
	public static EnumChatFormatting darkRed = EnumChatFormatting.DARK_RED;

	public static EnumChatFormatting pink = EnumChatFormatting.LIGHT_PURPLE;
	
	public static EnumChatFormatting underline = EnumChatFormatting.UNDERLINE;
	
   	public static String usu = " ?§r§{0,3}§8§8\\[§r§f§fUsu§r§8§8\\]| ?§8\\[§fUsu§8\\]";
   	public static String jup = " ?§r§{0,3}§8§8\\[§r§b§bJup§r§8§8\\]| ?§8\\[§bJup§8\\]";

    public static int swapAlpha(int color, float alpha) {
        int f = color >> 16 & 0xFF;
        int f1 = color >> 8 & 0xFF;
        int f2 = color & 0xFF;
        return getColor(f, f1, f2, (int) alpha);
    }
    
    public static void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0F;
        final float r = (hex >> 16 & 0xFF) / 255.0F;
        final float g = (hex >> 8 & 0xFF) / 255.0F;
        final float b = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }
    
	public static Vector4f getVectorFromColor(int color) {
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		return new Vector4f(red, green, blue, alpha);
	}
    
	public static float getAlphaByInt(int color) {        
        return (float)(color >> 24 & 255) / 255.0F;
	}
    
    public static Color colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        return colorSwitch(firstColor, secondColor, time, index, timePerIndex, speed, 255.0D);
    }
    
    public static int getColorFromPercentage(float percentage) {
        return Color.HSBtoRGB(Math.min(1.0F, Math.max(0.0F, percentage)) / 3, 0.9F, 0.9F);
    }

    public static Color colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed, double alpha) {
        long now = (long) (speed * (double) System.currentTimeMillis() + (double) ((long) index * timePerIndex));
        float redDiff = (float) (firstColor.getRed() - secondColor.getRed()) / time;
        float greenDiff = (float) (firstColor.getGreen() - secondColor.getGreen()) / time;
        float blueDiff = (float) (firstColor.getBlue() - secondColor.getBlue()) / time;
        int red = Math.round((float) secondColor.getRed() + redDiff * (float) (now % (long) time));
        int green = Math.round((float) secondColor.getGreen() + greenDiff * (float) (now % (long) time));
        int blue = Math.round((float) secondColor.getBlue() + blueDiff * (float) (now % (long) time));
        float redInverseDiff = (float) (secondColor.getRed() - firstColor.getRed()) / time;
        float greenInverseDiff = (float) (secondColor.getGreen() - firstColor.getGreen()) / time;
        float blueInverseDiff = (float) (secondColor.getBlue() - firstColor.getBlue()) / time;
        int inverseRed = Math.round((float) firstColor.getRed() + redInverseDiff * (float) (now % (long) time));
        int inverseGreen = Math.round((float) firstColor.getGreen() + greenInverseDiff * (float) (now % (long) time));
        int inverseBlue = Math.round((float) firstColor.getBlue() + blueInverseDiff * (float) (now % (long) time));

        return now % ((long) time * 2L) < (long) time ? (new Color(inverseRed, inverseGreen, inverseBlue, (int) alpha)) : (new Color(red, green, blue, (int) alpha));
    }
    
    public static int getRedFromColor(int color) {
        return color >> 16 & 0xFF;
    }

    public static int getGreenFromColor(int color) {
        return color >> 8 & 0xFF;
    }

    public static int getBlueFromColor(int color) {
        return color & 0xFF;
    }
    
    public static int getAlphaFromColor(int color) {
        return color >> 24 & 0xFF;
    }
    
    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }
    
    public static Color darker(final Color c, final double FACTOR) {
        return new Color(Math.max((int) (c.getRed() * FACTOR), 0),
                Math.max((int) (c.getGreen() * FACTOR), 0),
                Math.max((int) (c.getBlue() * FACTOR), 0),
                c.getAlpha());
    }

    public static int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity);
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public static Color withAlpha(final Color color, final int alpha) {
        if (alpha == color.getAlpha()) return color;
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtil.clamp(0, 255, alpha));
    }
    
	public static Color getAlphaColor(Color color, int alpha) {
	    int clampedAlpha = MathHelper.clamp_int(alpha, 0, 255);
	    if (color.getAlpha() == clampedAlpha) {
	        return color;
	    }
	    return new Color(color.getRed(), color.getGreen(), color.getBlue(), clampedAlpha);
	}
    
	public static Color blend(Color color, Color color1, double d0) {
		float f = (float) d0;
		float f1 = 1.0F - f;
		float[] afloat = new float[3];
		float[] afloat1 = new float[3];
		color.getColorComponents(afloat);
		color1.getColorComponents(afloat1);
		return new Color(afloat[0] * f + afloat1[0] * f1, afloat[1] * f + afloat1[1] * f1,
				afloat[2] * f + afloat1[2] * f1);
	}
	
    public static int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 0.75F, 1.0F) | 0xFF000000;
    }
	
    public static Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5);
    }
    
	public static void color(Color color) {
		if (color == null)
			color = Color.white;
		GL11.glColor4d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
	}

	public static void color(double red, double green, double blue, double alpha) {
		GL11.glColor4d(red, green, blue, alpha);
	}

	public static void color(int color) {
		glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF),
				(byte) (color >> 24 & 0xFF));
	}

	public static void resetColor() {
		color(1, 1, 1, 1);
	}
	
    public static int interpolateColor2(Color color1, Color color2, float fraction) {
        int red = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * fraction);
        int green = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fraction);
        int alpha = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * fraction);
        try {
            return new Color(red, green, blue, alpha).getRGB();
        } catch (Exception ex) {
            return 0xffffffff;
        }
    }
	
    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        double inverse_percent;
        int redPart;
        if(offset > 1.0D) {
            inverse_percent = offset % 1.0D;
            redPart = (int)offset;
            offset = redPart % 2 == 0?inverse_percent:1.0D - inverse_percent;
        }
        inverse_percent = 1.0D - offset;
        redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offset);
        int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offset);
        int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }
    
    public static Color colorFromInt(int color) {
        Color c = new Color(color);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
    }
        
    public static int getColorFromTags(Entity entity) {
        if (entity == null) {
            return Color.WHITE.getRGB();
        }

        int i = 16777215;
        float red = 1.0f, green = 1.0f, blue = 1.0f;

        if (entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) entity;
            
            final ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) player.getTeam();
            if (scoreplayerteam != null) {
                final String colorPrefix = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());
                
                if (colorPrefix.length() >= 2) {
                    i = mc.fontRendererObj.getColorCode(colorPrefix.charAt(1));
                }
            }

            red = (i >> 16 & 0xFF) / 255.0f;
            green = (i >> 8 & 0xFF) / 255.0f;
            blue = (i & 0xFF) / 255.0f;
        }

        return new Color(red, green, blue).getRGB();
    }
}
