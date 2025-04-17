package cc.unknown.file;

import java.awt.Color;
import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.ui.click.Window;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.value.Value;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ColorValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;

public class Config extends Directory {
    public Config(String name) {
        super(name, new File(Haru.MAIN_DIR + "/configs", name + ".json"));
    }

    @Override
    public void load(JsonObject object) {
        for (Module module : getModuleManager().getModules()) {
            if (object.has(module.getName())) {
                JsonObject moduleObject = object.get(module.getName()).getAsJsonObject();

                if (moduleObject.has("enabled")) {
                    module.setEnabled(moduleObject.get("enabled").getAsBoolean());
                }

                if (moduleObject.has("keyBind")) {
                    module.setKeyBind(moduleObject.get("keyBind").getAsInt());
                }

                if (moduleObject.has("hidden")) {
                    module.setHidden(moduleObject.get("hidden").getAsBoolean());
                }

                if (moduleObject.has("values")) {
                    JsonObject valuesObject = moduleObject.get("values").getAsJsonObject();

                    for (Value value : module.getValues()) {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());

                            if (value instanceof SliderValue) {
                                ((SliderValue) value).setValue(theValue.getAsNumber().floatValue());
                            } else if (value instanceof BoolValue) {
                                ((BoolValue) value).set(theValue.getAsBoolean());
                            } else if (value instanceof ModeValue) {
                                ((ModeValue) value).set(theValue.getAsString());
                            } else if (value instanceof MultiBoolValue) {
                                MultiBoolValue multi = (MultiBoolValue) value;
                                multi.getToggled().forEach(opt -> opt.set(false));
                                if (!theValue.getAsString().isEmpty()) {
                                    for (String str : theValue.getAsString().split(", ")) {
                                        multi.getValues().stream()
                                                .filter(b -> b.getName().equalsIgnoreCase(str))
                                                .forEach(b -> b.set(true));
                                    }
                                }
                            } else if (value instanceof ColorValue) {
                                JsonObject colorValues = theValue.getAsJsonObject();
                                ((ColorValue) value).set(ColorUtil.applyOpacity(
                                        new Color(colorValues.get("rgb").getAsInt()),
                                        colorValues.get("alpha").getAsFloat()));
                            }
                        }
                    }
                }
            }
        }

        if (object.has("settings")) {
            JsonObject settingsObject = object.getAsJsonObject("settings");

            for (Window window : Haru.instance.getDropGui().getWindows()) {
                if (window.getCategory() == Category.SETTINGS) {
                    // Bool
                    for (BoolValue bool : window.getSettingBools()) {
                        if (settingsObject.has(bool.getName())) {
                            bool.set(settingsObject.get(bool.getName()).getAsBoolean());
                        }
                    }
                    // Slider
                    for (SliderValue slider : window.getSettingSlider()) {
                        if (settingsObject.has(slider.getName())) {
                            slider.setValue(settingsObject.get(slider.getName()).getAsNumber().floatValue());                            
                        }
                    }
                }
            }
        }
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();

        for (Module module : getModuleManager().getModules()) {
            JsonObject moduleObject = new JsonObject();
            
            moduleObject.addProperty("enabled", module.isEnabled());
            moduleObject.addProperty("keyBind", module.getKeyBind());
            moduleObject.addProperty("hidden", module.isHidden());

            JsonObject valuesObject = new JsonObject();
            for (Value value : module.getValues()) {
                if (value instanceof SliderValue) {
                    valuesObject.addProperty(value.getName(), ((SliderValue) value).get());
                } else if (value instanceof BoolValue) {
                    valuesObject.addProperty(value.getName(), ((BoolValue) value).get());
                } else if (value instanceof ModeValue) {
                    valuesObject.addProperty(value.getName(), ((ModeValue) value).get());
                } else if (value instanceof MultiBoolValue) {
                    valuesObject.addProperty(value.getName(), ((MultiBoolValue) value).isEnabled());
                } else if (value instanceof ColorValue) {
                    ColorValue colorValue = (ColorValue) value;
                    JsonObject colorValues = new JsonObject();
                    colorValues.addProperty("rgb", Color.HSBtoRGB(colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()));
                    colorValues.addProperty("alpha", colorValue.getAlpha());
                    valuesObject.add(colorValue.getName(), colorValues);
                }
            }

            moduleObject.add("values", valuesObject);
            object.add(module.getName(), moduleObject);
        }

        JsonObject settingsObject = new JsonObject();
        for (Window window : Haru.instance.getDropGui().getWindows()) {
            if (window.getCategory() == Category.SETTINGS) {
                for (BoolValue bool : window.getSettingBools()) {
                    settingsObject.addProperty(bool.getName(), bool.get());
                }
                for (SliderValue slider : window.getSettingSlider()) {                    
                    settingsObject.addProperty(slider.getName(), ((SliderValue) slider).get());
                }
            }
        }
        
        object.add("settings", settingsObject);

        return object;
    }
}