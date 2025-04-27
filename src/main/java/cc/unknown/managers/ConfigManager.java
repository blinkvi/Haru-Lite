package cc.unknown.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cc.unknown.Haru;
import cc.unknown.file.Config;
import cc.unknown.file.Directory;
import cc.unknown.util.Accessor;

public final class ConfigManager implements Accessor {

    private final Config setting = new Config("latest");

    public String currentConfig = "latest";

    private final List<Directory> configs = Arrays.asList(setting);

    public void init() {
        loadFiles();
    }

    public boolean load(Directory config) {
        if (config == null) {
            Haru.instance.getLogger().warn("Attempted to load a null configuration.");
            return false;
        }

        try (FileReader reader = new FileReader(config.getFile())) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
            config.load(jsonObject);
            Haru.instance.getLogger().info("Loaded: " + config.getName());
            return true;
        } catch (IOException e) {
            Haru.instance.getLogger().error("Failed to load: " + config.getName(), e);
            return false;
        } catch (Exception e) {
            Haru.instance.getLogger().error("Error processing: " + config.getName(), e);
            return false;
        }
    }

    public boolean save(Directory config) {
        if (config == null) {
            return false;
        }

        JsonObject jsonObject = config.save();
        String jsonString = getGSON().toJson(jsonObject);

        try (FileWriter writer = new FileWriter(config.getFile())) {
            writer.write(jsonString);
            Haru.instance.getLogger().info("Saved: " + config.getName());
            return true;
        } catch (IOException e) {
            Haru.instance.getLogger().error("Failed to save: " + config.getName(), e);
            return false;
        }
    }

    public void saveFiles() {
        for (Directory config : configs) {
            if (!save(config)) {
                Haru.instance.getLogger().warn("Failed to save: " + config.getName());
            }
        }
    }

    public void loadFiles() {
        for (Directory config : configs) {
            File configFile = config.getFile();
            
            if (!configFile.exists()) {
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write("{}");
                    Haru.instance.getLogger().info("Created empty file: " + configFile.getName());
                } catch (IOException e) {
                    Haru.instance.getLogger().error("Failed to create empty file: " + config.getName(), e);
                }
            }
            
            if (!load(config)) {
                Haru.instance.getLogger().warn("Failed to load: " + config.getName());
            }
        }
    }
}
