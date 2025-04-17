package cc.unknown.command.impl;

import cc.unknown.command.Command;
import cc.unknown.util.player.FriendUtil;

public final class FriendCom extends Command {

	public FriendCom() {
		super("friend");
	}

	@Override
	public void execute(final String[] args) {
	    if (args.length < 1) {
	        error("Usage: .friend <add/remove> [player]");
	        return;
	    }

	    String action = args[0].toLowerCase();

	    switch (action) {
	        case "list":
	            success(getFriendList());
	            return;

	        case "clear":
	            FriendUtil.removeFriends();
	            success("Friend list cleared.");
	            return;

	        case "add":
	        case "remove":
	            if (args.length < 2) {
	                error("Usage: .friend " + action + " <player>");
	                return;
	            }
	            String target = args[1];

	            if (action.equals("add")) {
	                FriendUtil.addFriend(target);
	                success(String.format("Added %s to friends list", target));
	            } else {
	                FriendUtil.removeFriend(target);
	                success(String.format("Removed %s from friends list", target));
	            }
	            return;

	        default:
	            error("Usage: .friend <add/remove/list/clear> [player]");
	    }
	}

	private String getFriendList() {
	    if (FriendUtil.getFriends().isEmpty()) {
	        return "Your friend list is empty.";
	    }

	    StringBuilder message = new StringBuilder("Friend list:\n");
	    for (String friend : FriendUtil.getFriends()) {
	        message.append("- ").append(friend).append("\n");
	    }
	    return message.toString();
	}
}