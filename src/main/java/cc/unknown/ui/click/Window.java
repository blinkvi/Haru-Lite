package cc.unknown.ui.click;

import java.awt.Color;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.complement.IComponent;
import cc.unknown.ui.click.impl.ModuleComponent;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontRenderer;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.util.render.shader.impl.GradientBlur;
import cc.unknown.util.value.impl.BoolValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Window implements IComponent {
    private final List<ModuleComponent> moduleComponents;
    private final List<BoolValue> settingBools = new ArrayList<>();
	private final GradientBlur gradientBlur = new GradientBlur();
    
    private final Category category;
    public float x, y, dragX, dragY;
    private float width = 100, height = 0;
    private boolean expand, dragging = false;

    public Window(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;
        
        this.settingBools.add(new BoolValue("NoHitDelay", null, false));
        this.settingBools.add(new BoolValue("NoJumpDelay", null, false));
        this.settingBools.add(new BoolValue("NoUseDelay", null, false));
        
        this.moduleComponents = Haru.instance.getModuleManager()
                .getModulesByCategory(category)
                .stream()
                .map(ModuleComponent::new)
                .collect(Collectors.toList());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	FontRenderer fontRenderer = FontUtil.getFontRenderer("interSemiBold.ttf", 15);
    	Color outlineColor = new Color(getModule(ClickGUI.class).outlineColor.get().getRGB());
    	
        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }
        
    	if (getModule(ClickGUI.class).pref.isEnabled("Shaders")) {
    		gradientBlur.set(x, y, (int) width, (int) height, 0);
    		RenderUtil.drawBloomShadow(x, y, width, height, 14, 18, outlineColor.getRGB(), true,  true, true, false, false);
    	}

        if (getModule(ClickGUI.class).pref.isEnabled("RoundedOutline")) {
            RoundedUtil.drawRoundOutline(x - 2.1f, y, width + 3.5f, height, 8, 0.7f, new Color(19, 19, 19, 160), outlineColor);
        } else {
            RenderUtil.drawBorderedRect(x - 2.1f, y, width + 3.5f, height, 1F, new Color(19, 19, 19, 160).getRGB(), outlineColor.getRGB());
        }

        float componentOffsetY = 15;
        
        if (expand) {
            if (category == Category.SETTINGS) {
            	for (BoolValue bool : settingBools) {
            	    FontUtil.getFontRenderer("interSemiBold.ttf", 12).drawString(bool.getName(), x + 5F, y + componentOffsetY + 4F, -1);

            	    float boxSize = 8F;
            	    float boxX = x + width - boxSize - 6F;
            	    float boxY = y + componentOffsetY + 2F;

            	    RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, new Color(36, 36, 36).getRGB());

            	    if (bool.get()) {
            	        RenderUtil.drawRoundedRect(boxX, boxY, boxSize, boxSize, 8f, getModule(ClickGUI.class).mainColor.get().getRGB());
            	    }
            	    componentOffsetY += 12;
            	}
            } else if (moduleComponents != null) {
                for (ModuleComponent module : moduleComponents) {
                    module.x = x;
                    module.y = y + componentOffsetY;
                    module.width = width;
                    module.drawScreen(mouseX, mouseY);
                    componentOffsetY += module.height;
                }
            }
        }

        height = componentOffsetY;

        String categoryName = category.getName().toUpperCase();
        float centeredX = (float) (x + (width - fontRenderer.getStringWidth(categoryName)) / 2F);
        fontRenderer.drawString(categoryName, centeredX, y + 5F, -1);

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        moduleComponents.forEach(module -> module.mouseReleased(mouseX, mouseY, state));
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean isHeaderHovered = isHovered(x, y, width, 14F, mouseX, mouseY);

        if (isHeaderHovered) {
            if (mouseButton == 0) {
                dragging = true;
                dragX = x - mouseX;
                dragY = y - mouseY;
            } else if (mouseButton == 1) {
                expand = !expand;
            }
        } else if (expand) {
            if (category == Category.SETTINGS) {
                float offsetY = 15;
                for (BoolValue boolValue : settingBools) {
                    if (isHovered(x, y + offsetY, width, 12, mouseX, mouseY)) {
                        boolValue.set(!boolValue.get());
                    }
                    offsetY += 12;
                }
            } else if (moduleComponents != null) {
                moduleComponents.forEach(module -> module.mouseClicked(mouseX, mouseY, mouseButton));
            }
        }

        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void rebuildModuleCache() {
    	moduleComponents.clear();
        @SuppressWarnings("unchecked")
		java.util.List<Module> sortedModules = (List<Module>) Haru.instance.getModuleManager().getModules();
        sortedModules.sort((o1, o2) -> Collator.getInstance().compare(o1.getName(), o2.getName()));
        sortedModules.forEach(module -> moduleComponents.add(new ModuleComponent(module)));
    }
}
