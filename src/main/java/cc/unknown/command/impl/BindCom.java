package cc.unknown.command.impl;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.command.ComInfo;
import cc.unknown.command.Command;
import cc.unknown.util.client.system.StringUtil;

@ComInfo(name = "b", description = "")
public final class BindCom extends Command {

	@Override
	public void execute(final String[] args) {
	    if (args.length < 1) {
	        error("Correct usage: .b <module> <key>");
	        return;
	    }

	    String action = args[0].toLowerCase();

	    switch (action) {
	        case "list":
	            StringBuilder bindList = new StringBuilder("Bind list:\n");
	            boolean[] hasBinds = {false};

	            Haru.modMngr.getModules().forEach(module -> {
	                if (module.getKeyBind() != 0) {
	                    bindList.append(module.getName()).append(": ")
	                            .append(Keyboard.getKeyName(module.getKeyBind())).append("\n");
	                    hasBinds[0] = true;
	                }
	            });

	            if (hasBinds[0]) {
	                success(bindList.toString());
	            } else {
	                success("No binds assigned.");
	            }
	            return;

	        case "clear":
	            Haru.modMngr.getModules().forEach(module -> module.setKeyBind(0));
	            success("All binds have been removed.");
	            return;
	    }

	    if (args.length < 2) return;

	    String moduleName = args[0].replace(" ", "").toLowerCase();
	    String keyName = args[1].toUpperCase();
	    boolean[] foundModule = {false};

	    Haru.modMngr.getModules().forEach(module -> {
	        if (!foundModule[0] && module.getName().replace(" ", "").equalsIgnoreCase(moduleName)) {
	            int keyBind = Keyboard.getKeyIndex(keyName);
	            if (keyBind == -1) {
	                error("Invalid key: " + keyName);
	                return;
	            }

	            module.setKeyBind(keyBind);
	            success("Set " + module.getName() + " to " +
	                    StringUtil.upperSnakeCaseToPascal(Keyboard.getKeyName(module.getKeyBind())) + ".");
	            foundModule[0] = true;
	        }
	    });

	    if (!foundModule[0]) {
	        error("Module not found: " + moduleName);
	    }
	}

}