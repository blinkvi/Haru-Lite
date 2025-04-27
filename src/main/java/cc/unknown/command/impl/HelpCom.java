package cc.unknown.command.impl;

import cc.unknown.Haru;
import cc.unknown.command.ComInfo;
import cc.unknown.command.Command;

@ComInfo(name = "help", description = "")
public final class HelpCom extends Command {
    
    @Override
    public void execute(final String[] args) {
        Haru.comMngr.getCommands().forEach(c -> success(c.getName()));
    }
}