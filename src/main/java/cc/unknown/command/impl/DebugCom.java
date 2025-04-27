package cc.unknown.command.impl;

import cc.unknown.command.ComInfo;
import cc.unknown.command.Command;
import cc.unknown.handlers.TransactionHandler;

@ComInfo(name = "debug", description = "")
public final class DebugCom extends Command {

    @Override
    public void execute(final String[] args) {
    	TransactionHandler.start();
    }
}