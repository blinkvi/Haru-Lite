package cc.unknown.ui.click.impl;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.module.Module;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.render.font.FontRenderer;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.MultiSlider;
import cc.unknown.value.impl.Slider;

public class ModuleRenderer extends Component {
    public float x, y, width, height;
    private final Module module;
    private final CopyOnWriteArrayList<Component> values = new CopyOnWriteArrayList<>();

    public ModuleRenderer(Module module) {
        this.module = module;
        module.getValues().stream()
        .forEach(value -> {
            if (value instanceof Bool) {
                values.add(new BooleanRenderer((Bool) value));
            } else if (value instanceof Slider) {
                values.add(new SliderRenderer((Slider) value));
            } else if (value instanceof Mode) {
                values.add(new ModeRenderer((Mode) value));
            } else if (value instanceof MultiBool) {
                values.add(new MultiBooleanRenderer((MultiBool) value));
            } else if (value instanceof MultiSlider) {
            	values.add(new MultiSliderRenderer((MultiSlider) value));
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	FontRenderer fontRenderer = FontUtil.getFontRenderer("interSemiBold.ttf", 15);
    	ClickGUI gui = getModule(ClickGUI.class);
    	
        Color colorMain = new Color(
        		gui.colorMain.getAsInt(0),
                gui.colorMain.getAsInt(1),
                gui.colorMain.getAsInt(2)
        );
    	
        int yOffset = 11;
        String moduleName = module.getName().substring(0, 1).toUpperCase() + module.getName().substring(1);
        
        float textWidth = (float) fontRenderer.width(moduleName);
        float textX = x + (width - textWidth) / 2F;
        
        fontRenderer.draw(moduleName, textX, y + 4F, module.isEnabled() ? new Color(colorMain.getRGB()).getRGB() : new Color(160, 160, 160).getRGB());

        if (module.isExpanded()) {
            AtomicInteger offset = new AtomicInteger(yOffset);

            values.stream()
                .filter(Component::isVisible)
                .forEach(component -> {
                    component.x = x;
                    component.y = y + offset.get();
                    component.width = width;
                    component.drawScreen(mouseX, mouseY);
                    offset.addAndGet((int) component.height);
                });

            yOffset = offset.get();
        }

        this.height = yOffset;

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(x, y, width, 10F, mouseX, mouseY)) {
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
            values.stream().forEach(value -> value.mouseClicked(mouseX, mouseY, mouseButton));
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
