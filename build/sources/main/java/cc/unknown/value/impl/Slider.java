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
        super(name, module, visible);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = 1;
    }

    public Slider(String name, Module module, double value, double min, double max, double increment) {
        super(name, module, () -> true);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public Slider(String name, Module module, double value, double min, double max) {
        super(name, module, () -> true);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = 1;
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