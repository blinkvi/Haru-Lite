package cc.unknown.ui.click.impl;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.value.impl.Slider;

public class SliderRenderer extends Component {

    private final Slider value;

    public SliderRenderer(Slider value) {
        this.value = value;
        height = 11;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	ClickGUI gui = getModule(ClickGUI.class);
        double minValue = value.getMin();
        double maxValue = value.getMax();
        double currentValue = value.get();

        double progress = (currentValue - minValue) / (maxValue - minValue);
        progress = Math.min(1, Math.max(0, progress));

        if (gui.roundedButtons.get() && gui.shaders.get()) {
            RoundedUtil.drawRound(x + 3F, y + 2, (width - 5) * (float) progress, height - 4, 2, new Color(gui.red2.getAsInt(), gui.green2.getAsInt(), gui.blue2.getAsInt()));
        } else {
            RenderUtil.drawRect(x + 3F, y + 2, (width - 5) * (float) progress, height - 4, new Color(gui.red2.getAsInt(), gui.green2.getAsInt(), gui.blue2.getAsInt()).getRGB());
        }

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), x + 5F, y + 4F, -1);

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawCenteredString(String.format("%.2f", currentValue), x + 88F, y + 4F, -1);

        if (Mouse.isButtonDown(0) && isHovered(x, y, width, height - 4, mouseX, mouseY)) {
            double mouseOffset = mouseX - (x + 3);
            double sliderWidth = width - 5;

            if (mouseOffset >= 0 && mouseOffset <= sliderWidth) {
                double val = (mouseOffset / sliderWidth) * (maxValue - minValue) + minValue;
                val = val - (val % value.getIncrement());
                value.setValue(val);
            } else if (mouseX < x + 3) {
                value.setValue(minValue);
            } else if (mouseX > x + 3 + sliderWidth) {
                value.setValue(maxValue);
            }
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}