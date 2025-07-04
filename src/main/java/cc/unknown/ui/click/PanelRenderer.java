package cc.unknown.ui.click;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import cc.unknown.Haru;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.click.impl.Component;
import cc.unknown.ui.click.impl.ModuleRenderer;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontRenderer;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import cc.unknown.util.render.shader.impl.GradientBlur;
import net.minecraft.client.gui.ScaledResolution;

public class PanelRenderer extends Component {
    private final List<ModuleRenderer> moduleComponents;
    private final GradientBlur gradientBlur = new GradientBlur();

    private final Category category;
    public float x, y;
	private float dragX;
	private float dragY;
    private float width = 100, height = 0;
    private boolean expand, dragging = false;

    private float relativeX, relativeY;

    public PanelRenderer(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;

        this.moduleComponents = Haru.instance.getModuleManager()
                .getModulesByCategory(category)
                .stream()
                .map(ModuleRenderer::new)
                .collect(Collectors.toList());

        updateRelative();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer fontRenderer = FontUtil.getFontRenderer("interSemiBold.ttf", 15);
        ClickGUI gui = getModule(ClickGUI.class);

        Color colorOutline = new Color(
                gui.colorOutline.getAsInt(0),
                gui.colorOutline.getAsInt(1),
                gui.colorOutline.getAsInt(2)
        );

        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
            updateRelative();
        }

        if (gui.shaders.get()) {
            gradientBlur.set(x, y, (int) width, (int) height, 0);
            RenderUtil.drawBloomShadow(x, y, width, height, 14, 18, colorOutline.getRGB());
        }

        if (gui.roundedOutline.get() && gui.shaders.get()) {
            RoundedUtil.drawRoundOutline(x - 2.1f, y, width + 3.5f, height, 8, 0.7f, new Color(19, 19, 19, 160), colorOutline);
        } else {
            RenderUtil.drawBorderedRect(x - 2.1f, y, width + 3.5f, height, 1F, new Color(19, 19, 19, 160).getRGB(), colorOutline.getRGB());
        }

        int componentOffsetY = 15;

        if (expand && moduleComponents != null) {
            AtomicInteger offsetY = new AtomicInteger(componentOffsetY);
            moduleComponents.forEach(module -> {
                module.x = x;
                module.y = y + offsetY.get();
                module.width = width;
                module.drawScreen(mouseX, mouseY);
                offsetY.addAndGet((int) module.height);
            });
            componentOffsetY = offsetY.get();
        }

        height = componentOffsetY;

        String categoryName = category.getName().toUpperCase();
        float centeredX = (float) (x + (width - fontRenderer.width(categoryName)) / 2F);
        fontRenderer.draw(categoryName, centeredX, y + 5F, -1);

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        moduleComponents.forEach(module -> module.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(x, y, width, 14F, mouseX, mouseY)) {
            if (mouseButton == 0) {
                dragging = true;
                dragX = x - mouseX;
                dragY = y - mouseY;
            } else if (mouseButton == 1) {
                expand = !expand;
            }
        } else if (expand && moduleComponents != null) {
            moduleComponents.forEach(module -> module.mouseClicked(mouseX, mouseY, mouseButton));
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateRelative();
    }

    public void updateRelative() {
        int w = new ScaledResolution(Accessor.mc).getScaledWidth();
        int h = new ScaledResolution(Accessor.mc).getScaledHeight();
        this.relativeX = x / w;
        this.relativeY = y / h;
    }

    public void updateAbsolute() {
        int w = new ScaledResolution(Accessor.mc).getScaledWidth();
        int h = new ScaledResolution(Accessor.mc).getScaledHeight();
        this.x = relativeX * w;
        this.y = relativeY * h;
    }

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getDragX() {
		return dragX;
	}

	public void setDragX(float dragX) {
		this.dragX = dragX;
	}

	public float getDragY() {
		return dragY;
	}

	public void setDragY(float dragY) {
		this.dragY = dragY;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public float getRelativeX() {
		return relativeX;
	}

	public void setRelativeX(float relativeX) {
		this.relativeX = relativeX;
	}

	public float getRelativeY() {
		return relativeY;
	}

	public void setRelativeY(float relativeY) {
		this.relativeY = relativeY;
	}

	public List<ModuleRenderer> getModuleComponents() {
		return moduleComponents;
	}

	public GradientBlur getGradientBlur() {
		return gradientBlur;
	}

	public Category getCategory() {
		return category;
	}
}