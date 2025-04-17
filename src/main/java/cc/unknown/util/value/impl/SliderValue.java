package cc.unknown.util.value.impl;

import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.util.value.Value;
import net.minecraft.util.MathHelper;

public class SliderValue extends Value {
    private float value;
    private float min;
    private float max;
    private float increment;

    public SliderValue(String name, Module module, float value, float min, float max, float increment, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public SliderValue(String name, Module module, float value, float min, float max, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = 1;
    }

    public SliderValue(String name, Module module, float value, float min, float max, float increment) {
        super(name, module, () -> true);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public SliderValue(String name, Module module, float value, float min, float max) {
        super(name, module, () -> true);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = 1;
    }

    public float get() {	    
        return MathHelper.clamp_float(value, getMin(), getMax());
    }

    public void setValue(float value) {
        this.value = MathHelper.clamp_float(value, getMin(), getMax());
    }

	public float getValue() {
		return value;
	}

	public float getMin() {
	    return min;
	}

	public float getMax() {
	    return max;
	}

	public float getIncrement() {
		return increment;
	}

}