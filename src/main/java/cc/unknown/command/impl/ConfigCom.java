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
                    warning("No hay configs guardadas.");
                } else {
                    success("Configs disponibles: " + String.join(", ", configs));
                }
        		break;
        	case "folder":
        	case "open":
        	case "abrir":
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(dir);
                        success("Se ha abierto la carpeta de configuraciones.");
                    } catch (IOException e) {
                        error("No se pudo abrir la carpeta.");
                        e.printStackTrace();
                    }
                } else {
                    warning("Abrir carpetas no es compatible en este sistema.");
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
                    success("Config cargada: " + name);
                } else {
                    warning("No se pudo cargar la config: " + name);
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
                        success("Config creada y establecida como actual: " + name);
                        handleSave(name);
                    } else {
                        warning("La config ya existe: " + name);
                    }
                } catch (IOException e) {
                    error("No se pudo crear la config: " + name);
                }
        		break;
        	case "remove":
        	case "delete":
        	case "rm":
        	case "eliminar":
        	case "borrar":
                if (configFile.exists()) {
                    if (configFile.delete()) {
                        success("Config eliminada: " + name);
                    } else {
                        warning("No se pudo eliminar la config: " + name);
                    }
                } else {
                    error("La config no existe: " + name);
                }
        		break;
        	}
        }
    }

    private void handleSave(String configName) {
        if (getCfgManager().save(new Config(configName))) {
            success("Config guardada: " + configName);
        } else {
            error("Error al guardar la config: " + configName);
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
