package cc.unknown.socket.util;

import java.io.IOException;

import cc.unknown.Haru;
import cc.unknown.socket.api.HookRetriever;
import cc.unknown.socket.util.webhook.DiscordWebhook;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.render.client.ChatUtil;

public class DiscordNotifier implements HookRetriever {

    private static final DiscordWebhook chatHook = new DiscordWebhook(NetworkUtil.getRaw("irc", host, "b"));
    private static final DiscordWebhook alertHook = new DiscordWebhook(NetworkUtil.getRaw("bridge", host, "e"));

    public static void sendChatMessage(String message) {
        if (chatHook == null || chatHook.url == null || chatHook.url.isEmpty()) {
            ChatUtil.display("IRC is under maintenance.");
            return;
        }

        send(chatHook, "IRC", "-# [IRC] " + Haru.getUser() + ": " + message);
    }
    
    public static void sendLoginAlert() {
        send(alertHook, "AUTH", "-# [LOG] User: " + Haru.getUser());
    }

    private static void send(DiscordWebhook webhook, String username, String content) {
        try {
            webhook.username = username;
            webhook.content = content;
            webhook.execute();
        } catch (IOException ignored) { }
    }
}