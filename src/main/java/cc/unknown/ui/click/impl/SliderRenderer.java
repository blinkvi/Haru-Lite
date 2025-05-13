package cc.unknown.ui.click.impl;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.client.math.MathUtil;
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
        float minValue = value.getMin();
        float maxValue = value.getMax();

        float progress = (value.get() - minValue) / (maxValue - minValue);

        if (getModule(ClickGUI.class).pref.isEnabled("RoundedButtons")) {
            RoundedUtil.drawRound(x + 3F, y + 2, (width - 5) * progress, height - 4, 2, new Color(getModule(ClickGUI.class).mainColor.get().getRGB()));
        } else {
            RenderUtil.drawRect(x + 3F, y + 2, (width - 5) * progress, height - 4, new Color(getModule(ClickGUI.class).mainColor.get().getRGB()));
        }

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), x + 5F, y + 4F, -1);
        
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawCenteredString(String.format("%.2f", value.getValue()), x + 88F, y + 4F, -1);
        
        if (isHovered(x, y, width - 4, height - 2, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            float raw = (mouseX - x) / (width - 5);
            float set = Math.max(minValue, raw * (maxValue - minValue) + minValue);
            set = (float) MathUtil.incValue(set, value.getIncrement());
            value.setValue(set);
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}