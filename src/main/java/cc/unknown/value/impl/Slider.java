package cc.unknown.value.impl;

import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.value.Value;
import net.minecraft.util.MathHelper;

public class Slider extends Value {
    private double value;
    private double min;
    private double max;
    private double increment;

    public Slider(String name, Module module, double value, double min, double max, double increment, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public Slider(String name, Module module, double value, double min, double max, Supplier<Boolean> visible) {
        this(name, module, value, min, max, 1.0, visible);
    }

    public Slider(String name, Module module, double value, double min, double max, double increment) {
        this(name, module, value, min, max, increment, () -> true);
    }

    public Slider(String name, Module module, double value, double min, double max) {
        this(name, module, value, min, max, 1.0, () -> true);
    }

    public double get() {	    
        return MathHelper.clamp_double(value, getMin(), getMax());
    }

    public void setValue(double value) {
        this.value = MathHelper.clamp_double(value, getMin(), getMax());
    }

    public double getValue() {
        return value;
    }

    public float getAsFloat() {
        return (float) value;
    }

    public int getAsInt() {
        return (int) value;
    }

    public long getAsLong() {
        return (long) value;
    }

	public double getMin() {
	    return min;
	}

	public double getMax() {
	    return max;
	}

	public double getIncrement() {
		return increment;
	}

}