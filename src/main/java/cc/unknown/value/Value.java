package cc.unknown.value;
import java.util.Optional;
import java.util.function.Supplier;

import cc.unknown.module.Module;

public abstract class Value {
    private final String name;
    public Supplier<Boolean> visible;

    public Supplier<Boolean> getVisible() {
		return visible;
	}

	public void setVisible(Supplier<Boolean> visible) {
		this.visible = visible;
	}

	public String getName() {
		return name;
	}

	public Value(String name, Module module, Supplier<Boolean> visible) {
        this.name = name;
        this.visible = visible;
        Optional.ofNullable(module).ifPresent(m -> m.addValue(this));
    }

    public Boolean canDisplay() {
        return this.visible.get();
    }
}