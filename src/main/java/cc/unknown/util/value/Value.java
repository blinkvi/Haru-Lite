package cc.unknown.util.value;
import java.util.Optional;
import java.util.function.Supplier;

import cc.unknown.module.Module;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Value {
    private final String name;
    public Supplier<Boolean> visible;

    public Value(String name, Module module, Supplier<Boolean> visible) {
        this.name = name;
        this.visible = visible;
        Optional.ofNullable(module).ifPresent(m -> m.addValue(this));
    }

    public Boolean canDisplay() {
        return this.visible.get();
    }
}