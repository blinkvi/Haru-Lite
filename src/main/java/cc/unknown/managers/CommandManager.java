package cc.unknown.managers;

import cc.unknown.command.Command;
import cc.unknown.command.impl.*;
import cc.unknown.util.structure.list.SList;

public final class CommandManager extends SList<Command> {

	private static final long serialVersionUID = 1L;

	public void init() {
        registerCommands(
            new BindCom(),
            new ConfigCom(),
            new FriendCom(),
            new HelpCom(),
            new NameCom(),
            new JoinCom(),
            new ToggleCom(),
            new DebugCom()
        );
    }

    private void registerCommands(Command... commands) {
    	this.addAll(commands);
    }

    public void add(final Command command) {
    	this.add(command);
    }

    public SList<Command> getCommands() {
        return this;
    }
}