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
        StickersType sticker = hud.stickersType.getMode(StickersType.class);

        try {
        	RenderUtil.image(new ResourceLocation(sticker.imagePath), (int) renderX, (int) renderY, (int) sticker.width, (int) sticker.height);
        } catch (Exception e) { }
    }
    
    @Override
    public boolean shouldRender() {
        return hud.isEnabled();
    }
}

