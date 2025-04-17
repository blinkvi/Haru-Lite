package cc.unknown.ui.click.impl;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.module.Module;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.complement.Component;
import cc.unknown.ui.click.complement.IComponent;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontRenderer;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.value.Value;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;

public class ModuleComponent implements IComponent {
    public float x, y, width, height;
    private final Module module;
    private final CopyOnWriteArrayList<Component> values = new CopyOnWriteArrayList<>();

    public ModuleComponent(Module module) {
        this.module = module;
        for (Value value : module.getValues()) {
            if (value instanceof BoolValue) {
                values.add(new BooleanComponent((BoolValue) value));
            } else if (value instanceof ColorValue) {
                values.add(new ColorComponent((ColorValue) value));
            } else if (value instanceof SliderValue) {
                values.add(new SliderComponent((SliderValue) value));
            } else if (value instanceof ModeValue) {
                values.add(new ModeComponent((ModeValue) value));
            } else if (value instanceof MultiBoolValue) {
                values.add(new MultiBooleanComponent((MultiBoolValue) value));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	FontRenderer fontRenderer = FontUtil.getFontRenderer("interSemiBold.ttf", 15);
    	
    	if (getModule(ClickGUI.class).pref.isEnabled("ToolTips")) { // tooltips
	        if (MathUtil.isHovered(x, y, width, 10F, mouseX, mouseY)) {
	            String description = module.getModuleInfo().description();
	            if (!description.isEmpty()) {
		            float tooltipWidth = (float) (fontRenderer.getStringWidth(description) + 10);
		            float tooltipX = mouseX + 10;
		            float tooltipY = mouseY + 10;
		            RenderUtil.drawRect(tooltipX, tooltipY - 3, tooltipWidth - 6, 10, new Color(0, 0, 0, 150).getRGB());
		            fontRenderer.drawString(description, tooltipX, tooltipY, new Color(255, 255, 255).getRGB());
	            }
	        }
    	}
        
        float yOffset = 11;
        String moduleName = module.getName().substring(0, 1).toUpperCase() + module.getName().substring(1);
        
        float textWidth = (float) fontRenderer.getStringWidth(moduleName);
        float textX = x + (width - textWidth) / 2F;
        
        fontRenderer.drawString(moduleName, textX, y + 4F, module.isEnabled() ? getModule(ClickGUI.class).mainColor.get().getRGB() : new Color(160, 160, 160).getRGB());

        if (module.isExpanded()) {
            for (Component component : values) {
                if (!component.isVisible()) continue;
                component.x = x;
                component.y = y + yOffset;
                component.width = width;
                component.drawScreen(mouseX, mouseY);
                yOffset += component.height;
            }
        }

        this.height = yOffset;

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MathUtil.isHovered(x, y, width, 10F, mouseX, mouseY)) {
            if (mouseButton == 1) {
                if (!module.getValues().isEmpty()) {
                    module.setExpanded(!module.isExpanded());
                }
            }

            if (mouseButton == 0) {
                module.toggle();
            }
        }

        if (module.isExpanded()) {
            for (Component value : values) {
                value.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
