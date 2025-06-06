package cc.unknown.value.impl;

import java.util.Arrays;
import java.util.Collections;
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
        this.modes = Collections.unmodifiableList(Arrays.asList(modes));
        this.index = findIndex(current);
    }

    public Mode(String name, Module module, String current, String... modes) {
        this(name, module, () -> true, current, modes);
    }

    public Mode(String name, Module module, Enum<?> current, Enum<?>... enumModes) {
        this(name, module, () -> true, current, enumModes);
    }

    public Mode(String name, Module module, Supplier<Boolean> visible, Enum<?> current, Enum<?>... enumModes) {
        super(name, module, visible);
        this.modes = Arrays.stream(enumModes).map(Enum::toString).collect(Collectors.toList());
        this.index = findIndex(current.toString());
    }

    private int findIndex(String mode) {
        int i = modes.indexOf(mode);
        return i >= 0 ? i : 0;
    }

    public boolean is(String mode) {
        return get().equalsIgnoreCase(mode);
    }

    public String get() {
        return index >= 0 && index < modes.size() ? modes.get(index) : modes.get(0);
    }

    public void set(String mode) {
        this.index = findIndex(mode);
    }

    public void set(int index) {
        if (index >= 0 && index < modes.size()) {
            this.index = index;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        set(index);
    }

    public String getMode() {
        return get();
    }

    public <T extends Enum<T>> T getMode(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.toString().equalsIgnoreCase(get())).findFirst().orElse(null);
    }

    public List<String> getModes() {
        return Collections.unmodifiableList(modes);
    }
}