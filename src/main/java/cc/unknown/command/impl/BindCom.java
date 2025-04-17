package cc.unknown.command.impl;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.module.Module;
import cc.unknown.util.client.system.StringUtil;

public final class BindCom extends Command {

    public BindCom() {
		super("b");
	}

    @Override
    public void execute(final String[] args) {
        if (args.length < 1) {
        	error("Uso correcto: .b <modulo> <tecla>");
            return;
        }

        String action = args[0].toLowerCase();
        
        switch (action) {
        case "list":
            boolean hasBinds = false;
            StringBuilder bindList = new StringBuilder("Lista de binds:\n");

            for (final Module module : Haru.instance.getModuleManager().getModules()) {
                if (module.getKeyBind() != 0) {
                    bindList.append(module.getName()).append(": ")
                            .append(Keyboard.getKeyName(module.getKeyBind())).append("\n");
                    hasBinds = true;
                }
            }

            if (hasBinds) {
                success(bindList.toString());
            } else {
                success("No hay binds asignados.");
            }
        	break;
        case "clear":
            for (final Module module : Haru.instance.getModuleManager().getModules()) {
                module.setKeyBind(0);
            }
            success("Se han eliminado todos los binds.");
        	break;
        }

        if (args.length < 2) {
            return;
        }

        String moduleName = args[0].replace(" ", "").toLowerCase();
        String keyName = args[1].toUpperCase();
        boolean foundModule = false;

        for (final Module module : Haru.instance.getModuleManager().getModules()) {
            if (module.getName().replace(" ", "").equalsIgnoreCase(moduleName)) {
                int keyBind = Keyboard.getKeyIndex(keyName);

                if (keyBind == -1) {
                    error("Tecla inválida: " + keyName);
                    return;
                }

                module.setKeyBind(keyBind);
                success("Set " + module.getName() + " to " +
                        StringUtil.upperSnakeCaseToPascal(Keyboard.getKeyName(module.getKeyBind())) + ".");
                foundModule = true;
                break;
            }
        }

        if (!foundModule) {
            error("No se encontró el módulo: " + moduleName);
        }
    }


}