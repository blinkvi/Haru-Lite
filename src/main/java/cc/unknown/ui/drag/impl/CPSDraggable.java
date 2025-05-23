package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.handlers.ClientHandler;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;

public class CPSDraggable extends Drag {
    public CPSDraggable() {
        super("CPS");
        this.x = 0f;
        this.y = 10;
    }

    @Override
    public void render(ScaledResolution sr) {
        float x = renderX;
        float y = renderY;

        float fontSize = 15f;
        float iconSize = 5.0f;
        float padding = 5.0F;

        String cpsText = ClientHandler.getLeftClickCounter() + " | " + ClientHandler.getRightClickCounter() + " CPS";
        float cpsWidth = (float) (FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).getStringWidth(cpsText) + iconSize * 2.5F + 10);

        width = iconSize * 2.5F + cpsWidth - 20 + padding * 2;
        height = 20;

        float adjustedX = Math.min(x, sr.getScaledWidth() - width);
        float adjustedY = Math.min(y, sr.getScaledHeight() - height);

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height - 2, 6.0F, new Color(getModule(Interface.class).backgroundColor(), true));
        FontUtil.getFontRenderer("neverlose.ttf", 24).drawString("e", adjustedX + padding, adjustedY + (height / 2) - 4, setting.color());
        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).drawString(cpsText, adjustedX + padding + iconSize * 2.5F, adjustedY + (height / 2) - 3, -1);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("CPS");
    }
}

