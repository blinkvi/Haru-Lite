package cc.unknown.command.impl;

import java.util.HashMap;

import cc.unknown.command.Command;
import cc.unknown.handlers.AutoJoinHandler;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class JoinCom extends Command {
	final HashMap<String, Item> hashMap;

	public JoinCom() {
		super("game");
		this.hashMap = new HashMap<>();
	}

	@Override
	public void execute(String[] args) {
	    if (args.length > 0 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("help"))) {
	        ChatUtil.display(getList());
	        return;
	    }

	    if (args.length < 2) {
	        warning("Usage: .game <mode> <lobby>");
	        return;
	    }

	    this.hashMap.put("sw", Items.bow);
	    this.hashMap.put("tsw", Items.arrow);
	    this.hashMap.put("bw", Items.bed);
	    this.hashMap.put("tnt", Items.gunpowder);
	    this.hashMap.put("pgames", Items.cake);
	    this.hashMap.put("arena", Items.diamond_sword);

	    String gameName = args[0];

	    if (!this.hashMap.containsKey(gameName)) {
	        warning("Invalid mode. Use: .game list");
	        return;
	    }

	    if (!args[1].matches("\\d+")) {
	        warning("Invalid lobby number.");
	        return;
	    }

	    int lobbyNumber = Integer.parseInt(args[1]);

	    if (lobbyNumber <= 0) {
	        warning("Lobby number must be greater than 0.");
	        return;
	    }

	    AutoJoinHandler.init(hashMap.get(gameName), lobbyNumber);
	}

    private String getList() {
        return "\n" +
                ColorUtil.green + " - " + ColorUtil.white + "sw" + ColorUtil.gray + " (Skywars)        \n" +
                ColorUtil.green + " - " + ColorUtil.white + "tsw" + ColorUtil.gray + " (Team Skywars)  \n" +
                ColorUtil.green + " - " + ColorUtil.white + "tnt" + ColorUtil.gray + " (Tnt Tag)       \n" +
                ColorUtil.green + " - " + ColorUtil.white + "bw" + ColorUtil.gray + " (Bedwars)        \n" +
                ColorUtil.green + " - " + ColorUtil.white + "pgames" + ColorUtil.gray + " (Party Games)\n" +
                ColorUtil.green + " - " + ColorUtil.white + "arena" + ColorUtil.gray + " (Arenapvp)    \n";
    }
}
