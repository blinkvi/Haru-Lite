package cc.unknown.command.impl;

import cc.unknown.command.ComInfo;
import cc.unknown.command.Command;
import cc.unknown.module.impl.utility.AutoGame;

@ComInfo(name = "autogame", description = "")
public final class GameCom extends Command {
    @Override
    public void execute(final String[] args) {
        AutoGame autoGame = getModule(AutoGame.class);

        if (args.length == 2) {
            String modalidad = args[0];
            String comando = args[1];
            
            autoGame.customGame = modalidad;
            autoGame.customCommand = comando;

            success("Game: " + modalidad + " Command: " + comando);
        } else {
            error("Use: .autogame <game> <command>");
        }
    }

}
