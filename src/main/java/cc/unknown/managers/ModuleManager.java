package cc.unknown.managers;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.combat.*;
import cc.unknown.module.impl.move.*;
import cc.unknown.module.impl.utility.*;
import cc.unknown.module.impl.visual.*;
import cc.unknown.util.structure.list.SList;

public final class ModuleManager extends SList<Module> {
	private static final long serialVersionUID = 1L;
    private static final Comparator<Module> MODULE_COMPARATOR = Comparator.comparing(Module::getName);
    private final Map<Class<? extends Module>, Module> registry = new HashMap<>();
    private final Map<Category, Set<Module>> categories = new EnumMap<>(Category.class);

    public ModuleManager() {
        for (Category category : Category.values()) {
            categories.put(category, new TreeSet<>(MODULE_COMPARATOR));
        }

        addModules(
        		// combat
        		AimAssist.class,
        		AutoClicker.class,
        		Teams.class,
        		//Reach.class,
        		//HitBox.class,
        		Piercing.class,
        		MoreKB.class,
        		Velocity.class,
        		
        		// utility
        		AutoTool.class,
        		NoItemRelease.class,
        		Blink.class,
        		SaveMoveKeys.class,
                Loader.class,
                AutoGame.class,
                FastPlace.class,
                FastMine.class,
                FreeLook.class,

                // Visual
                ClickGUI.class,
                Interface.class,
                ESP.class,
                FullBright.class,
                AntiDebuff.class,
                BedPlates.class,
                Cosmetics.class,
                BreakProgress.class,
                MusicPlayer.class,
                NameProtect.class,
                NameTags.class,
                Chams.class,
                
                // move
                BridgeAssist.class,
                NoClip.class,
                Clutch.class,
        		NoSlow.class,
        		Sprint.class
        );
    }

    @SafeVarargs
    public final void addModules(Class<? extends Module>... moduleClasses) {
        for (final Class<? extends Module> moduleClass : moduleClasses) {
            try {
                Module module = moduleClass.getDeclaredConstructor().newInstance();
                this.add(module);
                registry.put(moduleClass, module);
                Category category = moduleClass.getAnnotation(ModuleInfo.class).category();
                Set<Module> categoryModules = categories.get(category);
                categoryModules.add(module);
                categories.put(category, categoryModules);
            } catch (Exception e) {
                Haru.instance.getLogger().error("Failed to instantiate module: {}", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleClass) {
        return (T) registry.get(moduleClass);
    }

    public Module getModule(String name) {
        return this.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public SList<Module> getModules() {
        return this;
    }

    public Set<Module> getModulesByCategory(Category category) {
        return categories.get(category);
    }
}