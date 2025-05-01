package cc.unknown.command.impl;

import org.apache.commons.lang3.StringUtils;

import cc.unknown.command.Command;

public final class HelpCom extends Command {

    public HelpCom() {
        super("help");
    }
    
    @Override
    public void execute(final String[] args) {
        getInstance().getCmdManager().getCommands().forEach(command -> 
        success(StringUtils.capitalize(command.prefix()))
        
        		
        );
    }
}