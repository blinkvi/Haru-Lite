package cc.unknown.value.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cc.unknown.module.Module;
import cc.unknown.value.Value;

public class MultiBool extends Value {
    private final List<Bool> options;
    private int index;

    public MultiBool(String name, Module module, Supplier<Boolean> visible, List<Bool> options) {
        super(Objects.requireNonNull(name), Objects.requireNonNull(module), Objects.requireNonNull(visible));
        this.options = new ArrayList<>(Objects.requireNonNull(options));
    }

    public MultiBool(String name, Module module, List<Bool> options) {
        this(name, module, () -> true, options);
    }

    public boolean isEnabled(String name) {
        return options.stream().filter(opt -> opt.getName().equalsIgnoreCase(name)).map(Bool::get).findFirst().orElse(false);
    }

    public void set(String name, boolean value) {
        options.stream().filter(opt -> opt.getName().equalsIgnoreCase(name)).findFirst().ifPresent(opt -> opt.set(value));
    }

    public List<Bool> getToggled() {
        return options.stream().filter(Bool::get).collect(Collectors.toList());
    }

    public String isEnabled() {
        return options.stream().filter(Bool::get).map(Bool::getName).collect(Collectors.joining(", "));
    }
    
    public void set(int index, boolean value) {
        if (isValidIndex(index)) {
            options.get(index).set(value);
        }
    }
    
    public boolean isEnabled(int index) {
        return isValidIndex(index) && options.get(index).get();
    }

    public List<Bool> getValues() {
        return getOptions();
    }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

    public List<Bool> getOptions() {
        return Collections.unmodifiableList(options);
    }
	
    private boolean isValidIndex(int index) {
        return index >= 0 && index < options.size();
    }
}
