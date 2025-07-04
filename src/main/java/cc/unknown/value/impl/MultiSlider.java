package cc.unknown.value.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cc.unknown.module.Module;
import cc.unknown.value.Value;

public class MultiSlider extends Value {

    private final List<Slider> options;
    private int index;

    public MultiSlider(String name, Module module, Supplier<Boolean> visible, List<Slider> options) {
        super(Objects.requireNonNull(name), Objects.requireNonNull(module), Objects.requireNonNull(visible));
        this.options = new ArrayList<>(Objects.requireNonNull(options));
    }

    public MultiSlider(String name, Module module, List<Slider> options) {
        this(name, module, () -> true, options);
    }

    public Double getValue(String name) {
        return options.stream()
                .filter(opt -> opt.getName().equalsIgnoreCase(name))
                .map(Slider::getValue)
                .findFirst()
                .orElse(null);
    }

    public void set(String name, double value) {
        options.stream()
                .filter(opt -> opt.getName().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(opt -> opt.setValue(value));
    }

    public List<Slider> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public List<Slider> getModified() {
        return options.stream()
                .filter(slider -> slider.getValue() != slider.getValue())
                .collect(Collectors.toList());
    }

    public double get(int index) {
        return isValidIndex(index) ? options.get(index).getValue() : 0.0;
    }
    
    public float getAsFloat(int index) {
        return (float) get(index);
    }
    
    public int getAsInt(int index) {
    	return (int) get(index);
    }
    
    public long getAsLong(int index) {
    	return (long) get(index);
    }
    
    public float getAsFloat(String name) {
        Double value = getValue(name);
        return value != null ? value.floatValue() : 0.0f;
    }
    
    public int getAsInt(String name) {
    	Double value = getValue(name);
    	return value != null ? value.intValue() : 0;
    }
    
    public long getAsLong(String name) {
    	Double value = getValue(name);
    	return value != null ? value.longValue() : 0L;
    }

    public void set(int index, double value) {
        if (isValidIndex(index)) {
            options.get(index).setValue(value);
        }
    }

    public void setIndex(int index) {
        if (isValidIndex(index)) {
            this.index = index;
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < options.size();
    }

    public String getValuesAsString() {
        return options.stream()
                .map(slider -> slider.getName() + "=" + String.format("%.2f", slider.getValue()))
                .collect(Collectors.joining(", "));
    }

	public int getIndex() {
		return index;
	}
}