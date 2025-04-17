package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.module.impl.visual.NameProtect;
import net.minecraft.client.gui.GuiScreen;

public final class NameCom extends Command {

    public NameCom() {
        super("ign");
    }

    private String lastMessage = "";

    @Override
    public void execute(final String[] args) {
        String name = mc.thePlayer.getName();
        NameProtect nameProtect = getModule(NameProtect.class);

        if (args.length == 0) {
            GuiScreen.setClipboardString(name);
            success("Nick copiado - [" + name + "]");
            return;
        }

        if (args.length == 1 && !args[0].trim().isEmpty()) {
            String newName = args[0].trim();
            
            if (!newName.equals(nameProtect.name)) {
                nameProtect.name = newName;
                if (!lastMessage.isEmpty()) {
                    success("Nick sustituido por - [" + newName + "]");
                } else {
                    success("Nick sustituido por - [" + newName + "]");
                }
                
                lastMessage = "Nick sustituido por - [" + newName + "]";
            }
        } else {
            error("Uso incorrecto. Usa: .ign [nombre]");
        }
    }

}
