package cc.unknown.util.value.impl;

import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.util.value.Value;

public class BoolValue extends Value {
    private boolean value;

    public BoolValue(String name, Module module, boolean value, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.value = value;
    }

    public BoolValue(String name, Module module, boolean value) {
        super(name, module, () -> true);
        this.value = value;
    }

    public BoolValue(String name, boolean value) {
        super(name, null, () -> true);
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void toggle() {
        value = !value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}