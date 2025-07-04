package cc.unknown.ui.drag.impl;

import java.awt.Color;
import java.util.ArrayList;

import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.font.FontRenderer;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionStatusDraggable extends Drag {
	
    public PotionStatusDraggable() {
        super("PotionStatus");

        this.x = 4;
        this.y = 4f;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        ArrayList<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        float posX = renderX;
        float fontSize = 13;
        float padding = 5;

        String title = "Potions";

        FontRenderer fontRendererRegular = FontUtil.getFontRenderer("interRegular.ttf", (int) fontSize);
        FontRenderer fontRendererSemiBold = FontUtil.getFontRenderer("interSemiBold.ttf", (int) fontSize);

        float maxWidth = (float) (fontRendererSemiBold.width(title) + padding * 2);
        float localHeight = fontRendererRegular.getHeight() + padding * 2 + 3f;

        RoundedUtil.drawRound(posX, renderY, width, height, 8, new Color(getModule(Interface.class).backgroundColor(), true));

        fontRendererSemiBold.drawCentered(title, posX + width / 2, renderY + padding + 2, -1);

        FontUtil.getFontRenderer("nursultan.ttf", 14).draw("E", posX + width - 16, renderY + 9, hud.color(0));

        final float[] posYRef = {renderY + fontRendererRegular.getHeight() + padding * 2 + 3f};
        final float[] localHeightRef = {localHeight};
        final float[] maxWidthRef = {maxWidth};

        potions.stream().forEach(effect -> {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion == null) return;

            String potionName = I18n.format(potion.getName());
            String durationText = Potion.getDurationString(effect);
            String nameText = potionName + (effect.getAmplifier() > 0 ? " " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1)) : "");

            float nameWidth = (float) fontRendererRegular.width(nameText);
            float durationWidth = (float) fontRendererRegular.width(durationText);
            float localWidth = nameWidth + durationWidth + padding * 3;

            fontRendererRegular.draw(nameText, posX + padding, posYRef[0] + 2, -1);
            fontRendererRegular.draw(durationText, posX + width - padding - durationWidth, posYRef[0] + 2, -1);

            maxWidthRef[0] = Math.max(maxWidthRef[0], localWidth);

            posYRef[0] += fontRendererRegular.getHeight() + padding;
            localHeightRef[0] += fontRendererRegular.getHeight() + padding;
        });

        width = Math.max(maxWidthRef[0], 80);
        height = localHeightRef[0] + 2.5f;

        width = Math.min(width, sr.getScaledWidth() - renderX);
        height = Math.min(height, sr.getScaledHeight() - renderY);
    }

    @Override
    public boolean shouldRender() {
        return hud.isEnabled() && hud.elements.isEnabled("PotionStatus");
    }
}
