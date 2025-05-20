package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.util.render.shader.impl.GradientBlur;
import net.minecraft.client.gui.ScaledResolution;

public class PlayerPositionDraggable extends Drag {
    public PlayerPositionDraggable() {
        super("PlayerPosition");
        this.x = 3f;
        this.y = 5f;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null) return;

        float x = renderX;
        float y = renderY;

        float fontSize = 15f;
        float iconSize = 5.0f;
        float padding = 5.0F;

        String playerPosition = (int) mc.thePlayer.posX + " " + (int) mc.thePlayer.posY + " " + (int) mc.thePlayer.posZ;
        float positionWidth = (float) FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).getStringWidth(playerPosition);

        width = iconSize * 2.5F + positionWidth + padding * 2;
        height = 20;

        float adjustedX = Math.min(x, sr.getScaledWidth() - width);
        float adjustedY = Math.min(y, sr.getScaledHeight() - height);

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height - 2, 4.0F, new Color(getModule(Interface.class).backgroundColor(), true));
    	if (setting.shaders.get()) {
    		new GradientBlur().set((int) adjustedX, (int) adjustedY, (int) width, (int) height, 0);
    		RenderUtil.drawBloomShadow(adjustedX, adjustedY, width, height, 20, 6, setting.color(0));
    	}
        FontUtil.getFontRenderer("nursultan.ttf", 18).drawString("F", adjustedX + padding, adjustedY + (height / 2) - 3, setting.color());
        FontUtil.getFontRenderer("interMedium.ttf", (int) fontSize).drawString(playerPosition, adjustedX + padding + iconSize * 2.5F, adjustedY + (height / 2) - 3, -1);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("PlayerPosition");
    }
}
