package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.util.render.shader.impl.GradientBlur;
import net.minecraft.client.gui.ScaledResolution;

public class PingDraggable extends Drag {
    public PingDraggable() {
        super("Ping");
        this.x = 0f;
        this.y = 0f;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null) return;

        float x = renderX;
        float y = renderY;

        float fontSize = 15f;
        float iconSize = 5.0f;
        float padding = 5.0F;

        String pingText = NetworkUtil.getPing(mc.thePlayer) + " ms";
        float pingWidth = (float) FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).getStringWidth(pingText);

        width = iconSize * 2.5F + pingWidth + padding * 2;
        height = 20;

        float adjustedX = Math.min(x, sr.getScaledWidth() - width);
        float adjustedY = Math.min(y, sr.getScaledHeight() - height);

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height - 2, 4.0F, new Color(getModule(Interface.class).backgroundColor(), true));
    	if (setting.shaders.get()) {
    		new GradientBlur().set((int) adjustedX, (int) adjustedY, (int) width, (int) height, 0);
    		RenderUtil.drawBloomShadow(adjustedX, adjustedY, width, height, 20, 6, setting.color(0), true, false, false, false, false);
    	}
        FontUtil.getFontRenderer("nursultan.ttf", 18).drawString("Q", adjustedX + padding, adjustedY + (height / 2) - 3, setting.color());
        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).drawString(pingText, adjustedX + padding + iconSize * 2.5F, adjustedY + (height / 2) - 3, -1);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Ping");
    }
}
