package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class FPSDraggable extends Drag {
    public FPSDraggable() {
        super("FPS");
        this.x = 0f;
        this.y = 0f;
    }

    @Override
    public void render(ScaledResolution sr) {
        float x = renderX;
        float y = renderY;

        float fontSize = 15f;
        float iconSize = 5.0f;
        float padding = 5.0F;

        int fps = Minecraft.getDebugFPS();
        String fpsText = fps + " FPS";
        float fpsWidth = (float) (padding + iconSize * 2.5F + FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).getStringWidth(fpsText));

        width = fpsWidth - 5 + padding * 2;
        height = 20;

        float adjustedX = Math.min(x, sr.getScaledWidth() - width);
        float adjustedY = Math.min(y, sr.getScaledHeight() - height);

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height - 2, 4.0F, new Color(getModule(Interface.class).backgroundColor(), true));

        FontUtil.getFontRenderer("nursultan.ttf", 18).drawString("X", adjustedX + padding, adjustedY + (height / 2) - 3, setting.color());
        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).drawString(fpsText, adjustedX + padding + iconSize * 2.5F, adjustedY + (height / 2) - 3, -1);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("FPS");
    }
}
