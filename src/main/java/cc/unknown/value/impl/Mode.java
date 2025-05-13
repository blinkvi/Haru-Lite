package cc.unknown.value.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cc.unknown.module.Module;
import cc.unknown.value.Value;

public class Mode extends Value {
    private int index;
    private final List<String> modes;

    public Mode(String name, Module module, Supplier<Boolean> visible, String current, String... modes) {
        super(name, module, visible);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(current);
    }

    public Mode(String name, Module module, String current, String... modes) {
        super(name, module, () -> true);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(current);
    }
    
    public Mode(String name, Module module, Enum<?> defaultMode, Enum<?>... enumModes) {
        super(name, module, () -> true);
        this.modes = Arrays.stream(enumModes).map(Enum::toString).collect(Collectors.toList());
        this.index = this.modes.indexOf(defaultMode.toString());
    }

    public Mode(String name, Module module, Supplier<Boolean> visible, Enum<?> current, Enum<?>... enumModes) {
        super(name, module, visible);
        this.modes = Arrays.stream(enumModes).map(Enum::toString).collect(Collectors.toList());
        this.index = this.modes.indexOf(current.toString());
    }

    public boolean is(String mode) {
        return get().equals(mode);
    }

    public String get() {
        if (index < 0 || index >= modes.size()) {
            return modes.get(0);
        }
        return modes.get(index);
    }

    public void set(String mode) {
        int newIndex = modes.indexOf(mode);
        if (newIndex != -1) {
            this.index = newIndex;
        }
    }

    public void set(int mode) {
        if (mode >= 0 && mode < modes.size()) {
            this.index = mode;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (index >= 0 && index < modes.size()) {
            this.index = index;
        }
    }

    public String getMode() {
        if (index < 0 || index >= modes.size()) {
            index = 0;
        }
        return modes.get(index);
    }
    
    public <T extends Enum<T>> T getMode(Class<T> enumClass) {
        String mode = get();
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.toString().equalsIgnoreCase(mode)) {
                return constant;
            }
        }
        return null;
    }

    public void setMode(String mode) {
        int newIndex = modes.indexOf(mode);
        if (newIndex != -1) {
            this.index = newIndex;
        }
    }

    public List<String> getModes() {
        return modes;
    }
}