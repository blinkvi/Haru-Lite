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
import cc.unknown.file.Directory;
import cc.unknown.file.cosmetics.CosmeticFile;
import cc.unknown.util.Accessor;

public final class CosmeticManager implements Accessor {

    private final CosmeticFile setting = new CosmeticFile("cosmetic");
    private final List<Directory> configs = Arrays.asList(setting);

    public void init() {
        loadFiles();
    }

    public boolean load(Directory dir) {
        if (dir == null) {
            Haru.instance.getLogger().warn("Attempted to load a null configuration.");
            return false;
        }

        try (FileReader reader = new FileReader(dir.getFile())) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
            dir.load(jsonObject);
            Haru.instance.getLogger().info("Loaded: " + dir.getName());
            return true;
        } catch (IOException e) {
            Haru.instance.getLogger().error("Failed to load: " + dir.getName(), e);
            return false;
        } catch (Exception e) {
            Haru.instance.getLogger().error("Error processing: " + dir.getName(), e);
            return false;
        }
    }

    public boolean save(Directory dir) {
        if (dir == null) return false;

        JsonObject jsonObject = dir.save();
        String jsonString = getGSON().toJson(jsonObject);

        try (FileWriter writer = new FileWriter(dir.getFile())) {
            writer.write(jsonString);
            Haru.instance.getLogger().info("Saved: " + dir.getName());
            return true;
        } catch (IOException e) {
            Haru.instance.getLogger().error("Failed to save: " + dir.getName(), e);
            return false;
        }
    }

    public void saveFiles() {
        for (Directory dir : configs) {
            if (!save(dir)) {
                Haru.instance.getLogger().warn("Failed to save: " + dir.getName());
            }
        }
    }

    public void loadFiles() {
        for (Directory dir : configs) {
            File configFile = dir.getFile();

            if (!configFile.exists()) {
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write("{}");
                    Haru.instance.getLogger().info("Created empty file: " + configFile.getName());
                } catch (IOException e) {
                    Haru.instance.getLogger().error("Failed to create empty file: " + dir.getName(), e);
                }
            }

            if (!load(dir)) {
                Haru.instance.getLogger().warn("Failed to load: " + dir.getName());
            }
        }
    }
}
