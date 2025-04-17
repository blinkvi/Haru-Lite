package cc.unknown.util.value.impl;

import java.awt.Color;
import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorValue extends Value {
    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private float alpha = 1;

    public ColorValue(String name, Module module, Color color, Supplier<Boolean> visible) {
        super(name, module, visible);
        set(color);
    }

    public ColorValue(String name, Module module, Color color) {
        super(name, module, () -> true);
        set(color);
    }

    public Color get() {
        return ColorUtil.applyOpacity(Color.getHSBColor(hue, saturation, brightness), alpha);
    }

    public void set(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = color.getAlpha() / 255.0f;
    }
}