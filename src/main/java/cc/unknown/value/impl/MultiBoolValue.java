package cc.unknown.value.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cc.unknown.module.Module;
import cc.unknown.value.Value;

public class MultiBoolValue extends Value {
    private final List<BoolValue> options;
    private int index;
    private float animation;

    public MultiBoolValue(String name, Module module, Supplier<Boolean> visible, List<BoolValue> options) {
        super(name, module, visible);
        this.options = new ArrayList<>(options);
        this.index = options.size();
    }

    public MultiBoolValue(String name, Module module, List<BoolValue> options) {
        super(name, module, () -> true);
        this.options = new ArrayList<>(options);
        this.index = options.size();
    }

    public boolean isEnabled(String name) {
        return this.options.stream()
            .filter(option -> option.getName().equalsIgnoreCase(name))
            .findFirst()
            .map(BoolValue::get)
            .orElse(false);
    }

    public void set(String name, boolean value) {
        this.options.stream()
            .filter(option -> option.getName().equalsIgnoreCase(name))
            .findFirst()
            .ifPresent(option -> option.set(value));
    }

    public List<BoolValue> getToggled() {
        return this.options.stream()
            .filter(BoolValue::get)
            .collect(Collectors.toList());
    }

    public String isEnabled() {
        return this.options.stream()
            .filter(BoolValue::get)
            .map(BoolValue::getName)
            .collect(Collectors.joining(", "));
    }

    public void set(int index, boolean value) {
        if (index >= 0 && index < options.size()) {
            this.options.get(index).set(value);
        }
    }

    public boolean isEnabled(int index) {
        return index >= 0 && index < options.size() && this.options.get(index).get();
    }

    public List<BoolValue> getValues() {
        return this.options;
    }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public float getAnimation() {
		return animation;
	}

	public void setAnimation(float animation) {
		this.animation = animation;
	}

	public List<BoolValue> getOptions() {
		return options;
	}
}
