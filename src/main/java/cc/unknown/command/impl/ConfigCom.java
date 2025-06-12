package cc.unknown.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.file.Config;

public final class ConfigCom extends Command {

    public ConfigCom() {
        super("cfg");
    }

    @Override
    public void execute(final String[] args) {
        String command = args[0];
        File dir = Haru.CFG_DIR;

        if (args.length == 1) {
            switch (command) {
                case "list":
                case "lista":
                case "listar":
                    String[] configs = getConfigList();
                    if (configs.length == 0) {
                        warning("There are no saved configs.");
                    } else {
                        success("Available configs: " + String.join(", ", configs));
                    }
                    break;
                case "folder":
                case "open":
                case "abrir":
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(dir);
                            success("The config folder has been opened.");
                        } catch (IOException e) {
                            error("Failed to open the folder.");
                            e.printStackTrace();
                        }
                    } else {
                        warning("Opening folders is not supported on this system.");
                    }
                    break;
            }
        }

        if (args.length == 2) {
            String name = args[1];
            Config cfg = new Config(name);
            File configFile = new File(dir, name + ".json");

            switch (command) {
                case "load":
                    if (getCfgManager().load(cfg)) {
                        getCfgManager().currentConfig = name;
                        success("Config loaded: " + name);
                    } else {
                        warning("Could not load the config: " + name);
                    }
                    break;
                case "guardar":
                case "save":
                    this.handleSave(name);
                    break;
                case "crear":
                case "create":
                    try {
                        if (configFile.createNewFile()) {
                            getCfgManager().currentConfig = name;
                            success("Config created and set as current: " + name);
                            handleSave(name);
                        } else {
                            warning("The config already exists: " + name);
                        }
                    } catch (IOException e) {
                        error("Could not create the config: " + name);
                    }
                    break;
                case "remove":
                case "delete":
                case "rm":
                case "eliminar":
                case "borrar":
                    if (configFile.exists()) {
                        if (configFile.delete()) {
                            success("Config deleted: " + name);
                        } else {
                            warning("Could not delete the config: " + name);
                        }
                    } else {
                        error("The config does not exist: " + name);
                    }
                    break;
            }
        }
    }

    private void handleSave(String configName) {
        if (getCfgManager().save(new Config(configName))) {
            success("Config saved: " + configName);
        } else {
            error("Failed to save the config: " + configName);
        }
    }

    private String[] getConfigList() {
        File directory = Haru.CFG_DIR;
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) {
            return new String[0];
        }
        String[] configs = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            configs[i] = files[i].getName().replaceFirst("\\.json$", "");
        }
        return configs;
    }
}