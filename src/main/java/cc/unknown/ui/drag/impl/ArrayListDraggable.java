package cc.unknown.ui.drag.impl;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.impl.GradientBlur;
import net.minecraft.client.gui.ScaledResolution;

public class ArrayListDraggable extends Drag {

    private final int PADDING = 2;
    
    private final GradientBlur gradientBlur = new GradientBlur();

    public ArrayListDraggable() {
        super("ArrayList");
        this.x = 10;
        this.y = 80;
    }

    @Override
    public void render(ScaledResolution sr) {
        if (!shouldRender()) return;

        int middle = sr.getScaledWidth() / 2;
        List<Module> enabledModules = getEnabledModules();

        float offset = 0;
        float lastWidth = 0;

        for (int i = 0; i < enabledModules.size(); i++) {
            Module module = enabledModules.get(i);
            int width = getModuleWidth(module);
            int height = getModuleHeight() - 2;

            renderModule(module, renderX, renderY, offset, width, height, 1.0f, middle, lastWidth, i, enabledModules.size());

            if (!module.isHidden()) {
                offset = calculateNextOffset(module, height, offset);
            }
            lastWidth = width;
        }
    }

    private List<Module> getEnabledModules() {
        return Haru.instance.getModuleManager().getModules().stream()
            .filter(module -> !module.isHidden() && module.isEnabled() && module.shouldDisplay(this))
            .sorted(Comparator.comparing(this::getModuleWidth).reversed())
            .collect(Collectors.toList());
    }

    private int getModuleWidth(Module module) {
        return (int) FontUtil.getFontRenderer("consolas.ttf", (int) setting.fontSize.get()).getStringWidth(module.getName());
    }

    private int getModuleHeight() {
        return FontUtil.getFontRenderer("consolas.ttf", (int) setting.fontSize.get()).getHeight();
    }

    private void renderModule(Module module, float localX, float localY, float offset, int width, int height,  float alphaAnimation, int middle, float lastWidth, int index, int totalModules) {
        if (setting.background.get()) {
            renderBackground(localX, localY, offset, width, height, middle,index);
        }

        renderText(module, localX, localY, offset, width, alphaAnimation, middle, index);
    }

    private void renderBackground(float localX, float localY, float offset, int width, int height, int middle, int index) {
        if (localX < middle) {
        	if (setting.shaders.get()) {
        		gradientBlur.set(localX - PADDING, localY + offset, width + 3, (int) (height + PADDING + setting.textHeight.get()), 0);
        		RenderUtil.drawBloomShadow(localX - PADDING, localY + offset, width + 3, height + PADDING + setting.textHeight.get(), 14, 18, setting.color(index * 200), true, false, false, false, false);
        	}
            RenderUtil.drawRoundedRect(localX - PADDING, localY + offset, width + 3, height + PADDING + setting.textHeight.get(), 8, setting.backgroundColor(index));
        } else {
        	if (setting.shaders.get()) {
        		gradientBlur.set(localX + this.width - 4 - width, localY + offset + 2, width + 3, (int) (height + PADDING + setting.textHeight.get()), 0);
        		RenderUtil.drawBloomShadow(localX + this.width - 4 - width, localY + offset + 2, width + 3, height + PADDING + setting.textHeight.get(), 14, 18, setting.color(index * 200), true, false, false, false, false);
        	}
            RenderUtil.drawRoundedRect(localX + this.width - 4 - width, localY + offset + 2, width + 3, height + PADDING + setting.textHeight.get(), 8, setting.backgroundColor(index));
        }
    }

    private void renderText(Module module, float localX, float localY, float offset, int width, float alphaAnimation, int middle, int index) {
        String text = module.getName();
        int color = ColorUtil.swapAlpha(setting.color(index), (int) alphaAnimation * setting.mainColor.get().getAlpha());
        float textY = localY + offset + 6;

        if (localX < middle) {
        	FontUtil.getFontRenderer("consolas.ttf", (int) setting.fontSize.get()).drawStringWithShadow(text, localX, textY - 3, color);
        } else {
            float textX = localX - width + this.width - 2;
            FontUtil.getFontRenderer("consolas.ttf", (int) setting.fontSize.get()).drawStringWithShadow(text, textX, textY - 1, color);
        }
    }

    private float calculateNextOffset(Module module, int height, float offset) {
        return (float) (offset + (1 * (height + setting.textHeight.get())) + PADDING);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("ArrayList");
    }
}
