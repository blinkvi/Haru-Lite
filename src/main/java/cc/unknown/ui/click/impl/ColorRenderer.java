package cc.unknown.ui.click.impl;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.Palette;

public class ColorRenderer extends Component {

    private final Palette value;
    private boolean expanded;

    public ColorRenderer(Palette value) {
        this.value = value;
        height = 11;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        HSBData data = new HSBData(value.get());
        final float[] hsba = {
                data.hue,
                data.saturation,
                data.brightness,
                value.get().getAlpha(),
        };
        
        RenderUtil.drawRoundedRect(x + 86, y + 1F, 8F, 8F, 8f, value.get().getRGB());
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), x +  5f, 5.5f + y, 0xffffffff);

        if (expanded) {
            RenderUtil.drawRect(x + 98 + 3, y, 61, 61, new Color(0, 0, 0));
            RenderUtil.drawRect(x + 98 + 3.5F, 0.5F + y, 60, 60, Color.getHSBColor(hsba[0], 1, 1));
            
            RenderUtil.drawHorizontalGradientSideways(x + 98F + 3.5F, 0.5F + y, 60, 60, Color.getHSBColor(hsba[0], 0, 1).getRGB(), 0x00F);
            RenderUtil.drawVerticalGradientSideways(x + 98 + 3.5f, 0.5F + y, 60, 60, 0x00F, Color.getHSBColor(hsba[0], 1, 0).getRGB());
            
            RenderUtil.drawRect(x + 98 + 3.5f + hsba[1] * 60 - .5f, 0.5F + ((1 - hsba[2]) * 60) - .5f + y, 1.5f, 1.5f, new Color(0, 0, 0));
            RenderUtil.drawRect(x + 98 + 3.5F + hsba[1] * 60,  0.5F + ((1 - hsba[2]) * 60) + y, .5f, .5f, value.get());

            final boolean onSB = isHovered(x + 98 + 3, y + 0.5F, width + 40, height + 70, mouseX, mouseY);
            final boolean onHue = isHovered(x + 98 + 67, y, 10, 70, mouseX, mouseY);

            if (onHue && Mouse.isButtonDown(0)) {
            	data.hue = Math.min(Math.max((mouseY - y) / 60F, 0), 1);
                value.set(data.getAsColor());
            } else if (onSB && Mouse.isButtonDown(0)) {
                data.saturation = Math.min(Math.max((mouseX - (x + 98) - 3) / 60F, 0), 1);
                data.brightness = 1 - Math.min(Math.max((mouseY - y - height) / 60F, 0), 1);
                value.set(data.getAsColor());
            }
            
            RenderUtil.drawRect(x + 98 + 67, y, 10, 61, new Color(0, 0, 0));

            for (float f = 0F; f < 5F; f += 1F) {
                final Color lasCol = Color.getHSBColor(f / 5F, 1F, 1F);
                final Color tarCol = Color.getHSBColor(Math.min(f + 1F, 5F) / 5F, 1F, 1F);
                RenderUtil.drawVerticalGradientSideways(x + 98 + 67.5F, 0.5F + f * 12 + y, 9, 12, lasCol.getRGB(), tarCol.getRGB());
            }

            RenderUtil.drawRect(x + 98 + 67.5F, -1 + hsba[0] * 60 + y, 9, 2, new Color(0, 0, 0));
            RenderUtil.drawRect(x + 98 + 67.5F, -0.5f + hsba[0] * 60 + y, 9, 1, new Color(204, 198, 255));
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(x ,y, 100F, height, mouseX, mouseY)) {
            if (mouseButton == 1) {
                expanded = !expanded;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
    
    private class HSBData {

        public float hue, saturation, brightness, alpha = 1;

        public HSBData(Color color) {
            final float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            this.hue = hsbColor[0];
            this.saturation = hsbColor[1];
            this.brightness = hsbColor[2];
        }

        public Color getAsColor() {
            final Color beforeReAlpha = Color.getHSBColor(hue, saturation, brightness);
            return new Color(beforeReAlpha.getRed(), beforeReAlpha.getGreen(), beforeReAlpha.getBlue(), Math.round(255 * alpha));
        }
    }
}
