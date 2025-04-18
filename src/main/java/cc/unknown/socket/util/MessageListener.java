package cc.unknown.socket.util;

import cc.unknown.handlers.DiscordHandler;
import cc.unknown.socket.api.HookRetriever;

public class MessageListener implements HookRetriever {

	public static void send(String message) {
		Webhook irc = new Webhook(endpoint);
		irc.username = "IRC";
		irc.content = "-# [IRC] " + DiscordHandler.getUser() + ": " + message;
		irc.execute();
	}
}