package cc.unknown.ui.click.impl;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.value.impl.MultiSlider;
import cc.unknown.value.impl.Slider;

public class MultiSliderRenderer extends Component {

    private final MultiSlider value;
    private boolean expanded;

    public MultiSliderRenderer(MultiSlider value) {
        this.value = value;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0;
        ClickGUI gui = getModule(ClickGUI.class);

        if (expanded) {
            RenderUtil.drawRect(x + 3F, y, width - 5, height, new Color(17, 17, 17, 160));
        }

        FontUtil.getFontRenderer("interSemiBold.ttf", 13)
            .drawCentered(value.getName() + "...", x + 50F, y + 4F, -1);

        if (expanded) {
            for (Slider sliderValue : value.getOptions()) {
                offset += 11;

                double minValue = sliderValue.getMin();
                double maxValue = sliderValue.getMax();
                double currentValue = sliderValue.get();
                double progress = (currentValue - minValue) / (maxValue - minValue);
                progress = Math.max(0, Math.min(1, progress));

                float sliderX = x + 3F;
                float sliderY = y + offset + 2;
                float sliderWidth = width - 5;
                float sliderHeight = 7;

                Color colorMain = new Color(
                		gui.colorMain.getAsInt(0),
                        gui.colorMain.getAsInt(1),
                        gui.colorMain.getAsInt(2)
                );

                if (gui.roundedButtons.get() && gui.shaders.get()) {
                    RoundedUtil.drawRound(sliderX, sliderY, sliderWidth * (float) progress, sliderHeight, 2, colorMain);
                } else {
                    RenderUtil.drawRect(sliderX, sliderY, sliderWidth * (float) progress, sliderHeight, colorMain.getRGB());
                }

                FontUtil.getFontRenderer("interSemiBold.ttf", 13).draw(sliderValue.getName(), x + 5F, y + 4F + offset, -1);
                boolean isInteger = Math.abs(currentValue % 1) < 0.001;
                String display = isInteger ? String.format("%.0f", currentValue) : String.format("%.2f", currentValue);

                float xOffset = isInteger ? 4F : 0F;

                FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawCentered(display, x + 88F + xOffset, y + 4F + offset, -1);
                
                if (Mouse.isButtonDown(0) && isHovered(sliderX, sliderY, sliderWidth + 5, sliderHeight, mouseX, mouseY)) {
                    double mouseOffset = mouseX - (x + 3);

                    if (mouseOffset >= 0 && mouseOffset <= sliderWidth) {
                        double val = (mouseOffset / sliderWidth) * (maxValue - minValue) + minValue;
                        val = val - (val % sliderValue.getIncrement());
                        sliderValue.setValue(val);
                    } else if (mouseX < x + 3) {
                    	sliderValue.setValue(minValue);
                    } else if (mouseX > x + 3 + sliderWidth) {
                    	sliderValue.setValue(maxValue);
                    }
                }
            }
        }

        this.height = offset + 11;
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    	float offset = 0;
        
        if (isHovered(x, y, width, 10, mouseX, mouseY)) {
            if (mouseButton == 0) {
                expanded = !expanded;
            }
        }

        if (expanded) {
            for (Slider sliderValue : value.getOptions()) {
                offset += 11;
                if (isHovered(x, y + offset, width, 10, mouseX, mouseY)) {
                    if (mouseButton == 0) {
                    	sliderValue.setValue(sliderValue.getValue());
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
