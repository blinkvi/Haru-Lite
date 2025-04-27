package cc.unknown.file;

import java.io.File;
import java.util.Objects;

import com.google.gson.JsonObject;

import cc.unknown.util.Managers;

public abstract class Directory implements Managers {
    private final File file;
    private final String name;

    public Directory(String name, File file) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.file = Objects.requireNonNull(file, "file cannot be null");
    }

    public abstract void load(JsonObject object);

    public abstract JsonObject save();

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }
}