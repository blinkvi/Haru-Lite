package cc.unknown.module;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cc.unknown.Haru;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.drag.impl.ArrayListDraggable;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.list.SList;
import cc.unknown.value.Value;
import cc.unknown.value.impl.Slider;
import net.minecraftforge.common.MinecraftForge;

public abstract class Module implements Accessor {

	private final SList<Value> values = new SList<>();
	
    private final ModuleInfo moduleInfo;
    private final String name;
    private final String description;
    private final Category category;
    private int keyBind;
    
    private boolean hidden;
    private boolean state;
    private boolean expanded;
    
    protected Module() {
        this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        Objects.requireNonNull(moduleInfo, "ModuleInfo annotation is missing on " + getClass().getName());
        this.name = moduleInfo.name();
        this.description = moduleInfo.description();
        this.category = moduleInfo.category();
        this.keyBind = moduleInfo.key();
    }

    public void onEnable() { }
    public void onDisable() { }
    public boolean isEnabled() { return state; }
    public boolean isDisabled() { return !state; }
    
    @SafeVarargs
    public final boolean isEnabled(Class<? extends Module>... modules) {
        for (Class<? extends Module> module : modules) {
            if (getModule(module).isEnabled()) return true;
        }
        return false;
    }

    public <M extends Module> boolean isEnabled(Class<M> module) {
        Module mod = Haru.instance.getModuleManager().getModule(module);
        return mod != null && mod.isEnabled();
    }

    public <M extends Module> boolean isDisabled(Class<M> module) {
        Module mod = Haru.instance.getModuleManager().getModule(module);
        return mod == null || mod.isDisabled();
    }

	public void toggle() {
        setEnabled(!isEnabled());
    }

    public void setEnabled(boolean enabled) {
        if (this.state != enabled) {
            this.state = enabled;
            if (enabled) {
                enable();
            } else {
                disable();
            }
        }
    }
    
    private void enable() {
    	MinecraftForge.EVENT_BUS.register(this);
    	try {
            onEnable();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        try {
            onDisable();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (mc.thePlayer != null) {
            e.printStackTrace();
        }
    }

    public <M extends Module> M getModule(Class<M> clazz) {
        return Haru.instance.getModuleManager().getModule(clazz);
    }

    public void addValues(Value... settings) {
        values.addAll(Arrays.asList(settings));
    }

    public void addValue(Value value) {
        addValues(value);
    }

    public Value getValue(String valueName) {
        return values.stream()
                .filter(value -> value.getName().equalsIgnoreCase(valueName))
                .findFirst()
                .orElse(null);
    }

    public boolean shouldDisplay(ArrayListDraggable arrayListDraggable) {
        if (this instanceof ClickGUI) {
            return false;
        }
        
        Interface interfaces = Haru.instance.getModuleManager().getModule(Interface.class);
        Map<Category, Boolean> visibility = new HashMap<>();
        visibility.put(Category.COMBAT, interfaces.hideCategory.isEnabled("Combat"));
        visibility.put(Category.MOVE, interfaces.hideCategory.isEnabled("Move"));
        visibility.put(Category.UTILITY, interfaces.hideCategory.isEnabled("Utility"));
        visibility.put(Category.VISUAL, interfaces.hideCategory.isEnabled("Visual"));

        Category curCategory = this.getModuleInfo().category();
        if (visibility.getOrDefault(curCategory, false)) {
            return false;
        }

        return true;
    }

    public void correctValues(Slider min, Slider max) {
		float minValue = min.getValue();
		float maxValue = max.getValue();

	    if (minValue >= maxValue) {
	        minValue = maxValue;
	        min.setValue(minValue);
	        
	        maxValue = minValue;
	        max.setValue(maxValue);
	    }
    }

	public SList<Value> getValues() {
		return values;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public int getKeyBind() {
		return keyBind;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isState() {
		return state;
	}

	public boolean isExpanded() {
		return expanded;
	}
	
    public void setKeyBind(int keyBind) {
		this.keyBind = keyBind;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
}