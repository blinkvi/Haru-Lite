package cc.unknown.command.impl;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.module.Module;

public final class ToggleCom extends Command {

    public ToggleCom() {
        super("t");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 1) {
            final String moduleName = args[0].replace(" ", "").toLowerCase();
            for (final Module module : Haru.instance.getModuleManager().getModules()) {
                if (module.getName().replace(" ", "").equalsIgnoreCase(moduleName)) {
                    module.toggle();
                    success(module.getName() + " has " + 
                            (module.isEnabled() ? "\u00a7AEnabled\u00a77." : "\u00a7CDisabled\u00a77."));
                    return;
                }
            }
            error("No se encontró el módulo: " + moduleName);
        } else {
            error("Uso correcto: .t <módulo>");
        }
    }

}