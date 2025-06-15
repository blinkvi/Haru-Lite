package cc.unknown.ui.drag.impl;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cc.unknown.module.Module;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.render.font.FontUtil;
import net.minecraft.client.gui.ScaledResolution;

public class ArrayListDraggable extends Drag {

    private final int PADDING = 2;
    
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

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        AtomicReference<Float> lastWidth = new AtomicReference<>(0f);

        IntStream.range(0, enabledModules.size())
            .mapToObj(i -> enabledModules.get(i))
            .forEach(module -> {
                int width = getModuleWidth(module);
                int height = getModuleHeight() - 2;

                renderModule(module, renderX, renderY, offset.get(), width, height, 1.0f, middle, lastWidth.get(), enabledModules.indexOf(module), enabledModules.size());

                if (!module.isHidden()) {
                    offset.set(calculateNextOffset(module, height, offset.get()));
                }

                lastWidth.set((float) width);
            });
    }

    private List<Module> getEnabledModules() {
        return getModuleManager().getModules().stream()
            .filter(module -> !module.isHidden() && module.isEnabled() && module.shouldDisplay(this))
            .sorted(Comparator.comparing(this::getModuleWidth).reversed())
            .collect(Collectors.toList());
    }

    private int getModuleWidth(Module module) {
        return (int) FontUtil.getConsolas(hud.fontSize.getAsInt()).getStringWidth(module.getName());
    }

    private int getModuleHeight() {
        return FontUtil.getConsolas(hud.fontSize.getAsInt()).getHeight();
    }

    private void renderModule(Module module, float localX, float localY, float offset, int width, int height,  float alphaAnimation, int middle, float lastWidth, int index, int totalModules) {
        if (hud.background.get()) {
            renderBackground(localX, localY, offset, width, height, middle,index);
        }

        renderText(module, localX, localY, offset, width, alphaAnimation, middle, index);
    }

    private void renderBackground(float localX, float localY, float offset, int width, int height, int middle, int index) {
        if (localX < middle) {
            RenderUtil.drawRoundedRect(localX - PADDING, localY + offset, width + 3, height + PADDING + hud.textHeight.get(), 8, hud.backgroundColor());
        } else {
            RenderUtil.drawRoundedRect(localX + this.width - 4 - width, localY + offset + 2, width + 3, height + PADDING + hud.textHeight.get(), 8, hud.backgroundColor());
        }
    }

    private void renderText(Module module, float localX, float localY, float offset, int width, float alphaAnimation, int middle, int index) {
        String text = module.getName();
        int color = ColorUtil.swapAlpha(hud.color(index), (int) alphaAnimation * new Color(hud.red.getAsInt(), hud.green.getAsInt(), hud.blue.getAsInt()).getAlpha());
        float textY = localY + offset + 6;

        if (localX < middle) {
        	FontUtil.getConsolas(hud.fontSize.getAsInt()).drawStringWithShadow(text, localX, textY - 3, color);
        } else {
            float textX = localX - width + this.width - 2;
            FontUtil.getConsolas(hud.fontSize.getAsInt()).drawStringWithShadow(text, textX, textY - 1, color);
        }
    }

    private float calculateNextOffset(Module module, int height, float offset) {
        return (float) (offset + (1 * (height + hud.textHeight.get())) + PADDING);
    }

    @Override
    public boolean shouldRender() {
        return hud.isEnabled() && hud.elements.isEnabled("ModuleList");
    }
}
