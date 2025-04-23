package cc.unknown.socket.util;

import cc.unknown.Haru;
import cc.unknown.socket.api.HookRetriever;
import cc.unknown.util.client.network.NetworkUtil;

public class MessageListener implements HookRetriever {

	public static void send(String message) {
		Webhook irc = new Webhook(NetworkUtil.getRaw("endpoint", host, "b"));
		irc.username = "IRC";
		irc.content = "-# [IRC] " + Haru.getUser() + ": " + message;
		irc.execute();
	}
}