package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.Haru;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;

public class WatermarkDraggable extends Drag {
    public WatermarkDraggable() {
        super("Watermark");
        this.x = 0f;
        this.y = 0f;
    }
    
    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        float x = renderX;
        float y = renderY;
        float fontSize = 15f;
        float padding = 5.0F;

        String title = Haru.NAME + " " + Haru.VERSION;
        float titleWidth = (float) FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).getStringWidth(title);

        width = 2.0f + titleWidth + padding * 2;
        height = 20;

        width = Math.min(width, sr.getScaledWidth() - renderX);
        height = Math.min(height, sr.getScaledHeight() - renderY);

        RoundedUtil.drawRound(x, y, width, height - 2, 6.0F, new Color(getModule(Interface.class).backgroundColor(), true));

        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).drawString(title, x + padding + 2.0f, y + (height / 2) - 3, setting.color());
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Watermark");
    }
}
