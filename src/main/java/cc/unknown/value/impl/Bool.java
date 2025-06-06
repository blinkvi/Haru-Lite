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
        this(name, module, value, () -> true);
    }

    public Bool(String name, boolean value) {
        this(name, null, value, () -> true);
    }

    public boolean get() {
        return value;
    }

    public boolean isEnabled() {
        return value;
    }

    public void toggle() {
        this.value = !this.value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Bool{name='%s', value=%s}", getName(), value);
    }
}