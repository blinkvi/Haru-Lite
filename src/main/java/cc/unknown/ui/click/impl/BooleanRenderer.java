package cc.unknown.ui.click.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.Bool;

public class BooleanRenderer extends Component {

    private final Bool value;
    private boolean expanded = false;

    public BooleanRenderer(Bool value) {
        this.value = value;
        height = 11;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	FontUtil.getFontRenderer("interSemiBold.ttf", 13).draw(value.getName(), x + 5F, y + 4F, -1);
    	ClickGUI gui = getModule(ClickGUI.class);

        float boxSize = 8F;
        float boxX = x + width - boxSize - 6F;
        float boxY = y + 2F;
        
        Color colorMain = new Color(
        		gui.colorMain.getAsInt(0),
                gui.colorMain.getAsInt(1),
                gui.colorMain.getAsInt(2)
        );

        RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, new Color(36, 36, 36).getRGB());

        if (value.get()) {
            RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, new Color(colorMain.getRGB()).getRGB());
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
