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
        this.x = 5f;
        this.y = 4f;
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
        float fpsWidth = (float) (padding + iconSize * 2.5F + FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).width(fpsText));

        width = fpsWidth - 5 + padding * 2;
        height = 20;

        float adjustedX = Math.min(x, sr.getScaledWidth() - width);
        float adjustedY = Math.min(y, sr.getScaledHeight() - height);

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height - 2, 6.0F, new Color(getModule(Interface.class).backgroundColor(), true));
        FontUtil.getFontRenderer("nursultan.ttf", 18).draw("X", adjustedX + padding, adjustedY + (height / 2) - 3, hud.color());
        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).draw(fpsText, adjustedX + padding + iconSize * 2.5F, adjustedY + (height / 2) - 3, -1);
    }

    @Override
    public boolean shouldRender() {
        return hud.isEnabled() && hud.elements.isEnabled("FPS");
    }
}
