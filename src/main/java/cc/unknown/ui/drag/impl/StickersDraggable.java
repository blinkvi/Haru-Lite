package cc.unknown.ui.drag.impl;

import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.enums.StickersType;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class StickersDraggable extends Drag {
    public StickersDraggable() {
        super("CPS");
        this.x = 30f;
        this.y = 50;
    }

    @Override
    public void render(ScaledResolution sr) {
        StickersType sticker = setting.stickersType.getMode(StickersType.class);

        if (sticker != null) {
            RenderUtil.image(
                new ResourceLocation(sticker.getImagePath()),
                (int) renderX,
                (int) renderY,
                (int) sticker.getWidth(),
                (int) sticker.getHeight()
            );
        }
    }
    
    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Stickers");
    }
}

