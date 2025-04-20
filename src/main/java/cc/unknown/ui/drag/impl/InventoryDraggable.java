package cc.unknown.ui.drag.impl;

import java.awt.Color;

import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.util.render.shader.impl.GradientBlur;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class InventoryDraggable extends Drag {
    public InventoryDraggable() {
        super("Inventory");
        this.x = 0f;
        this.y = 0.6f;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (mc.thePlayer == null || mc.thePlayer.inventory == null) return;

        float x = renderX;
        float y = renderY;
        float itemWidth = 14;
        float itemHeight = 14;
        float rowSpacing = 17.0F;
        float columnSpacing = 0.7F;

        width = itemWidth * 9.1F + columnSpacing * 9.0F;
        height = itemHeight * 3.0F + 19.0F + 4;

        float adjustedX = Math.min(x, sr.getScaledWidth() - width);
        float adjustedY = Math.min(y, sr.getScaledHeight() - height);

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height, 5.5F, new Color(setting.backgroundColor(), true));
    	if (setting.shaders.get()) {
    		new GradientBlur().set((int) adjustedX, (int) adjustedY, (int) width, (int) height, 0);
    		RenderUtil.drawBloomShadow(adjustedX, adjustedY, width, height, 20, 6, setting.color(0), true, false, false, false, false);
    	}
    	
        FontUtil.getFontRenderer("interSemiBold.ttf", 15).drawString("Inventory", adjustedX + 19.0F, adjustedY + 5.5F, new Color(255, 255, 255, 255).getRGB());
        FontUtil.getFontRenderer("nursultan.ttf", 16).drawString("A", adjustedX + 5.0F, adjustedY + 6.0F, -1);

        float startX = adjustedX + 0.7F;
        float startY = adjustedY + 17.5F;

        for (int i = 9; i < 36; ++i) {
            ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
            RenderUtil.renderItemStack(slot, startX, startY, 0.80F);

            startX += itemWidth + columnSpacing;

            if (i == 17 || i == 26) {
                startY += rowSpacing - 1;
                startX = adjustedX + 0.7F;
            }
        }
    }
    
    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Inventory");
    }
}
