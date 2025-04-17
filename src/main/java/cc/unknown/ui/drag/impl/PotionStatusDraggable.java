package cc.unknown.ui.drag.impl;

import java.awt.Color;
import java.util.ArrayList;

import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionStatusDraggable extends Drag {
	
    public PotionStatusDraggable() {
        super("PotionStatus");

        this.x = 0;
        this.y = 0.0f;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        ArrayList<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        float posX = renderX;
        float posY = renderY;
        float fontSize = 13;
        float padding = 5;

        String title = "Potions";

        float maxWidth = (float) (FontUtil.getFontRenderer("interSemiBold.ttf", (int) fontSize).getStringWidth(title) + padding * 2);
        float localHeight = FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).getHeight() + padding * 2 + 3f;

        RoundedUtil.drawRound(posX, posY, width, height, 6, new Color(getModule(Interface.class).backgroundColor(), true));
        FontUtil.getFontRenderer("interSemiBold.ttf", (int) fontSize).drawCenteredString(title, posX + width / 2, posY + padding + 2, -1);
        FontUtil.getFontRenderer("nursultan.ttf", 14).drawString("E", posX + width - 16, posY + 9, setting.color(0));

        posY += FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).getHeight() + padding * 2 + 3f;

        for (PotionEffect effect : potions) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion == null) continue;

            String potionName = I18n.format(potion.getName());
            String durationText = Potion.getDurationString(effect);
            String nameText = potionName + (effect.getAmplifier() > 0 ? " " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1)) : "");
            
            float nameWidth = (float) FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).getStringWidth(nameText);
            float durationWidth = (float) FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).getStringWidth(durationText);
            float localWidth = nameWidth + durationWidth + padding * 3;

            FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).drawString(nameText, posX + padding, posY + 2, -1);
            FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).drawString(durationText, posX + width - padding - durationWidth, posY + 2, -1);

            maxWidth = Math.max(maxWidth, localWidth);
            posY += FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).getHeight() + padding;
            localHeight += FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize).getHeight() + padding;
        }

        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;

        width = Math.min(width, sr.getScaledWidth() - renderX);
        height = Math.min(height, sr.getScaledHeight() - renderY);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("PotionStatus");
    }
}
