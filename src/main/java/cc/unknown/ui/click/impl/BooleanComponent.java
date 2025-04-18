package cc.unknown.ui.click.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.complement.Component;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.value.impl.BoolValue;

public class BooleanComponent extends Component {

    private final BoolValue value;
    private boolean expanded = false;

    public BooleanComponent(BoolValue value) {
        this.value = value;
        height = 11;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), x + 5F, y + 4F, -1);

        float boxSize = 8F;
        float boxX = x + width - boxSize - 6F;
        float boxY = y + 2F;

        RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, new Color(36, 36, 36).getRGB());

        if (value.get()) {
            RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, getModule(ClickGUI.class).mainColor.get().getRGB());
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(x, y, width, 9, mouseX, mouseY)) {
            if (mouseButton == 0) {
                expanded = !expanded;
            }
        }

        if (isHovered(x, y, width, 9, mouseX, mouseY)) {
            if (mouseButton == 0) {
                value.set(!value.get());
            }
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
