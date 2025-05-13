package cc.unknown.value.impl;

import java.util.function.Supplier;

import cc.unknown.module.Module;
import cc.unknown.value.Value;

public class Bool extends Value {
    private boolean value;

    public Bool(String name, Module module, boolean value, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.value = value;
    }

    public Bool(String name, Module module, boolean value) {
        super(name, module, () -> true);
        this.value = value;
    }

    public Bool(String name, boolean value) {
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