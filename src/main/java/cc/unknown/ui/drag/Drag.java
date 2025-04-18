package cc.unknown.ui.drag;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cc.unknown.Haru;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public abstract class Drag implements Accessor {
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("x")
    public float x;
    @Expose
    @SerializedName("y")
    public float y;
    protected float renderX, renderY;
    public float width;
    public float height;
    public boolean dragging;
    private int dragX, dragY;
    public int align;
    protected Interface setting = Haru.instance.getModuleManager().getModule(Interface.class);
    
    private int LEFT = 1;
    private int RIGHT = 2;
    private int TOP = 4;
    private int BOTTOM = 8;
    private int CENTER = 16;
    private int MIDDLE = 32;

    public Drag(String name) {
        this.name = name;
        this.x = 0f;
        this.y = 0f;
        this.width = 100f;
        this.height = 100f;
        this.align = LEFT | TOP;
    }

    public Drag(String name, int align) {
        this(name);
        this.align = align;
    }

    public abstract void render(ScaledResolution sr);
    
    public void updatePos(ScaledResolution sr) {
        renderX = x * sr.getScaledWidth();
        renderY = y * sr.getScaledHeight();

        if (renderX < 0f) x = 0f;
        if (renderX > sr.getScaledWidth() - width) x = (sr.getScaledWidth() - width) / sr.getScaledWidth();
        if (renderY < 0f) y = 0f;
        if (renderY > sr.getScaledHeight() - height) y = (sr.getScaledHeight() - height) / sr.getScaledHeight();

        if (align == (LEFT | TOP)) return;

        if ((align & RIGHT) != 0) {
            renderX -= width;
        } else if ((align & CENTER) != 0) {
            renderX -= width / 2f;
        }

        if ((align & BOTTOM) != 0) {
            renderY -= height;
        } else if ((align & MIDDLE) != 0) {
            renderY -= height / 2f;
        }
    }

    public final void onChatGUI(int mouseX, int mouseY, boolean drag, ScaledResolution sr) {
        boolean hovering = isHovered(renderX, renderY, width, height, mouseX, mouseY);

        if (dragging) {
        	RenderUtil.drawBorderedRect((float)renderX, (float)renderY, (float)width, (float)height, 2f, new Color(0, 0, 0, 0).getRGB(), Color.WHITE.getRGB());
        }

        if (hovering && Mouse.isButtonDown(0) && !dragging && drag) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        if (!Mouse.isButtonDown(0)) dragging = false;

        if (dragging) {
            float deltaX = (float) (mouseX - dragX) / sr.getScaledWidth();
            float deltaY = (float) (mouseY - dragY) / sr.getScaledHeight();

            x += deltaX;
            y += deltaY;

            dragX = mouseX;
            dragY = mouseY;
        }

    }

    public abstract boolean shouldRender();
}
