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
import cc.unknown.file.position.DragPosition;
import cc.unknown.file.position.GuiPosition;
import cc.unknown.util.Accessor;

public final class PositionManager implements Accessor {

    private static final String EMPTY_JSON = "{}";

    private final DragPosition elements = new DragPosition("elements");
    private final GuiPosition clickgui = new GuiPosition("clickgui");

    private final List<Directory> positions = Arrays.asList(elements, clickgui);

    public String currentConfig = "latest";

    public void init() {
        loadFiles();
    }

    public boolean load(Directory directory) {
        if (directory == null) {
            Haru.logger.warn("Tried to load null config.");
            return false;
        }

        try (FileReader reader = new FileReader(directory.getFile())) {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(reader).getAsJsonObject();
            directory.load(json);
            Haru.logger.info("Loaded position: " + directory.getName());
            return true;
        } catch (IOException e) {
            Haru.logger.error("I/O error loading position: " + directory.getName(), e);
        } catch (Exception e) {
            Haru.logger.error("Unexpected error loading position: " + directory.getName(), e);
        }
        return false;
    }

    public boolean save(Directory directory) {
        if (directory == null) {
            return false;
        }

        try (FileWriter writer = new FileWriter(directory.getFile())) {
            JsonObject json = directory.save();
            getGSON().toJson(json, writer);
            Haru.logger.info("Saved position: " + directory.getName());
            return true;
        } catch (IOException e) {
            Haru.logger.error("Failed to save position: " + directory.getName(), e);
        }
        return false;
    }

    public void saveFiles() {
        for (Directory directory : positions) {
            if (!save(directory)) {
                Haru.logger.warn("Could not save position: " + directory.getName());
            }
        }
    }

    public void loadFiles() {
        for (Directory directory : positions) {
            File file = directory.getFile();
            if (!file.exists()) {
                createEmptyJsonFile(file);
            }

            if (!load(directory)) {
                Haru.logger.warn("Could not load position: " + directory.getName());
            }
        }
    }

    private void createEmptyJsonFile(File file) {
        try {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                Haru.logger.warn("Could not create directories for: " + file.getAbsolutePath());
                return;
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(EMPTY_JSON);
            }

            Haru.logger.info("Created empty position file: " + file.getName());
        } catch (IOException e) {
            Haru.logger.error("Failed to create position file: " + file.getName(), e);
        }
    }
}