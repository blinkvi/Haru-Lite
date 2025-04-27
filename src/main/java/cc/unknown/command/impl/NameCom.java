package cc.unknown.command.impl;

import cc.unknown.command.ComInfo;
import cc.unknown.command.Command;
import cc.unknown.module.impl.visual.NameProtect;
import net.minecraft.client.gui.GuiScreen;

@ComInfo(name = "name", description = "")
public final class NameCom extends Command {

    private String lastMessage = "";

    @Override
    public void execute(final String[] args) {
        String name = mc.getSession().getUsername();
        NameProtect nameProtect = getModule(NameProtect.class);

        if (args.length == 0) {
            GuiScreen.setClipboardString(name);
            success("Nick copied - [" + name + "]");
            return;
        }

        if (args.length == 1 && !args[0].trim().isEmpty()) {
            String newName = args[0].trim();
            
            if (!newName.equals(nameProtect.name)) {
                nameProtect.name = newName;
                if (!lastMessage.isEmpty()) {
                    success("Nick changed to - [" + newName + "]");
                } else {
                    success("Nick changed to - [" + newName + "]");
                }
                
                lastMessage = "Nick changed to - [" + newName + "]";
            }
        } else {
            error("Incorrect usage. Use: .name [name]");
        }
    }

}
