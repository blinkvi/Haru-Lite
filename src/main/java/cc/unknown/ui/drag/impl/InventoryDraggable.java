package cc.unknown.ui.drag.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
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

        RoundedUtil.drawRound(adjustedX, adjustedY, width, height, 6F, new Color(setting.backgroundColor(), true));
        FontUtil.getFontRenderer("interSemiBold.ttf", 15).drawString("Inventory", adjustedX + 19.0F, adjustedY + 5.5F, new Color(255, 255, 255, 255).getRGB());
        FontUtil.getFontRenderer("nursultan.ttf", 16).drawString("A", adjustedX + 5.0F, adjustedY + 6.0F, -1);

        float startX = adjustedX + 0.7F;
        float startY = adjustedY + 17.5F;

        List<Integer> rowBreakIndexes = Arrays.asList(0, 27);

        IntStream.range(9, 36)
            .mapToObj(i -> {
                ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                float finalStartX = startX + (i - 9) % 9 * (itemWidth + columnSpacing);
                float finalStartY = startY + (i - 9) / 9 * rowSpacing;

                if (rowBreakIndexes.contains(i)) {
                    finalStartX = adjustedX + 0.7F;
                }

                return new Object[] {slot, finalStartX, finalStartY};
            })
            .forEach(obj -> {
                ItemStack slot = (ItemStack) obj[0];
                float xPos = (float) obj[1];
                float yPos = (float) obj[2];

                RenderUtil.renderItemStack(slot, xPos, yPos, 0.80F);
            });
    }
    
    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Inventory");
    }
}
