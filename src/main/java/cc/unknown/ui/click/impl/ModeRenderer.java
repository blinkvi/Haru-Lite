package cc.unknown.ui.click.impl;

import java.util.List;

import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.ModeValue;

public class ModeRenderer extends Component {

    private final ModeValue value;

    public ModeRenderer(ModeValue value) {
        this.value = value;
        height = 11;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.getName(), x + 5F, y + 4F, -1);
        FontUtil.getFontRenderer("interSemiBold.ttf", 13).drawString(value.get(),
                x + (width - 5) - FontUtil.getFontRenderer("interSemiBold.ttf", 13).getStringWidth(value.get()), y + 4F,
                -1);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MathUtil.isHovered(x, y, 100F, height, mouseX, mouseY)) {
            List<String> modes = value.getModes();
            
            if (modes.isEmpty()) {
                return;
            }

            int currentIndex = modes.indexOf(value.get());

            if (mouseButton == 0) {
                value.set(modes.get((currentIndex + 1) % modes.size()));
            } else if (mouseButton == 1) {
                value.set(modes.get((currentIndex - 1 + modes.size()) % modes.size()));
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
