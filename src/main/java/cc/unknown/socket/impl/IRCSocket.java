package cc.unknown.socket.impl;

import cc.unknown.socket.WebSocketCore;
import cc.unknown.socket.util.MessageListener;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.render.client.ColorUtil;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class IRCSocket extends WebSocketCore {
	
	public static void ircHandler(MessageReceivedEvent event) {
	    if (!event.getChannel().getId().equals(NetworkUtil.getRaw("irc_id", host, "a"))) {
	        return;
	    }
	    
	    String content = event.getMessage().getContentDisplay();
	    if (content.isEmpty()) {
	        return;
	    }
	    
	    lastMessage = content;

	    String username = format(event.getAuthor().getName());	    
	    if (lastMessage.startsWith("-# [IRC]") && !lastMessage.isEmpty()) {
	    	String extUser = extractUsername(content);
	    	String extContent = extractMessage(content);
	    	
		    ChatUtil.display(ColorUtil.pink + "[H] " + ColorUtil.darkAqua + extUser + ": " + ColorUtil.reset + extContent);
	    } else {
	    	ChatUtil.display(ColorUtil.blue + "[D] " + ColorUtil.red + username + ": " + ColorUtil.reset + content);
	    }
	}

	private static String extractUsername(String content) {
	    int startIdx = content.indexOf("]") + 2;
	    int endIdx = content.indexOf(":", startIdx);
	    if (startIdx > 0 && endIdx > startIdx) {
	        return content.substring(startIdx, endIdx).trim();
	    }
	    return "";
	}

	private static String extractMessage(String content) {
	    int startIdx = content.indexOf(":") + 1;
	    String message = content.substring(startIdx).trim();
	    
	    if (message.startsWith("``") && message.endsWith("``")) {
	        message = message.substring(2, message.length() - 2);
	    }
	    
	    return message;
	}

	public synchronized void sendMessage(String message) {
		TextChannel channel = jda.getTextChannelById(NetworkUtil.getRaw("irc_id", host, "a"));
		if (channel != null) {
			MessageListener.send(message);
		}
	}
	
	public static String format(String input) {
	    if (input == null || input.isEmpty()) {
	        return input;
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
