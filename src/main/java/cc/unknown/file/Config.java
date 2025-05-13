package cc.unknown.file;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Palette;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;

public class Config extends Directory {
    public Config(String name) {
        super(name, new File(Haru.MAIN_DIR + "/configs", name + ".json"));
    }

    @Override
    public void load(JsonObject object) {
        getModuleManager().getModules().forEach(module -> {
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

                    module.getValues().forEach(value -> {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());

                            if (value instanceof Slider) {
                                ((Slider) value).setValue(theValue.getAsNumber().floatValue());
                            } else if (value instanceof Bool) {
                                ((Bool) value).set(theValue.getAsBoolean());
                            } else if (value instanceof Mode) {
                                ((Mode) value).set(theValue.getAsString());
                            } else if (value instanceof MultiBool) {
                                MultiBool multi = (MultiBool) value;
                                multi.getToggled().forEach(opt -> opt.set(false));

                                if (!theValue.getAsString().isEmpty()) {
                                    Arrays.stream(theValue.getAsString().split(", "))
                                            .forEach(str ->
                                                    multi.getValues().stream()
                                                            .filter(b -> b.getName().equalsIgnoreCase(str))
                                                            .forEach(b -> b.set(true))
                                            );
                                }
                            } else if (value instanceof Palette) {
                                JsonObject colorValues = theValue.getAsJsonObject();
                                ((Palette) value).set(ColorUtil.applyOpacity(
                                        new Color(colorValues.get("rgb").getAsInt()),
                                        colorValues.get("alpha").getAsFloat()));
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();

        getModuleManager().getModules().forEach(module -> {
            JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("enabled", module.isEnabled());
            moduleObject.addProperty("keyBind", module.getKeyBind());
            moduleObject.addProperty("hidden", module.isHidden());

            JsonObject valuesObject = new JsonObject();

            module.getValues().forEach(value -> {
                if (value instanceof Slider) {
                    valuesObject.addProperty(value.getName(), ((Slider) value).get());
                } else if (value instanceof Bool) {
                    valuesObject.addProperty(value.getName(), ((Bool) value).get());
                } else if (value instanceof Mode) {
                    valuesObject.addProperty(value.getName(), ((Mode) value).get());
                } else if (value instanceof MultiBool) {
                    valuesObject.addProperty(value.getName(), ((MultiBool) value).isEnabled());
                } else if (value instanceof Palette) {
                    Palette colorValue = (Palette) value;
                    JsonObject colorValues = new JsonObject();
                    colorValues.addProperty("rgb", Color.HSBtoRGB(colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()));
                    colorValues.addProperty("alpha", colorValue.getAlpha());
                    valuesObject.add(colorValue.getName(), colorValues);
                }
            });

            moduleObject.add("values", valuesObject);
            object.add(module.getName(), moduleObject);
        });
        return object;
    }

}