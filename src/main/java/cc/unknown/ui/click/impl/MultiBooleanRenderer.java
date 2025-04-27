package cc.unknown.ui.click.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;

public class MultiBooleanRenderer extends Component {

    private final MultiBoolValue value;
    private boolean expanded;

    public MultiBooleanRenderer(MultiBoolValue value) {
        this.value = value;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0;

        if (expanded) {
            RenderUtil.drawRect(x + 3F, y, width - 5, height, new Color(17, 17, 17, 160));
        }

        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawCenteredString(value.getName() + "...", x + 50F, y + 4F, -1);

        if (expanded) {
            RenderUtil.drawRoundedRect(x + 88, y + 1F, 9F, 9F, 8f, new Color(17, 17, 17, 160).getRGB());

            for (BoolValue boolValue : value.getValues()) {
                offset += 11;

                FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(boolValue.getName(), x + 5F, y + 4F + offset, -1);

                float boxSize = 8F;
                float boxX = x + width - boxSize - 6F;
                float boxY = y + offset + 2F;

                RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, new Color(17, 17, 17, 160).getRGB());

                if (boolValue.get()) {
                    RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, getModule(ClickGUI.class).mainColor.get().getRGB());
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
            for (BoolValue boolValue : value.getValues()) {
                offset += 11;
                if (isHovered(x, y + offset, width, 10, mouseX, mouseY)) {
                    if (mouseButton == 0) {
                        boolValue.set(!boolValue.get());
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
