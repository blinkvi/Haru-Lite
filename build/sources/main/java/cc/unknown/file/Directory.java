package cc.unknown.file;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Objects;

import cc.unknown.util.Accessor;

public abstract class Directory implements Accessor {
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