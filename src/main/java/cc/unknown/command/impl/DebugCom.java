package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.handlers.TransactionHandler;

public final class DebugCom extends Command {
    
    public DebugCom() {
        super("debug");
    }
    
    @Override
    public void execute(final String[] args) {
    	TransactionHandler.start();
    }
}