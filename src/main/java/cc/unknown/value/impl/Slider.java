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
    
    public Slider(String name, double value, double min, double max, double increment) {
        this(name, null, value, min, max, increment, () -> true);
    }
    
    public Slider(String name, double value, double min, double max) {
        this(name, null, value, min, max, 1.0, () -> true);
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
        return MathHelper.clamp_double(value, min, max);
    }

    public void setValue(double value) {
        this.value = MathHelper.clamp_double(value, min, max);
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

	public void setIncrement(double increment) {
		this.increment = increment;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getValue() {
		return value;
	}

	public double getIncrement() {
		return increment;
	}
}