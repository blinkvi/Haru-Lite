package cc.unknown.socket;

import cc.unknown.socket.api.HookRetriever;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.socket.impl.IRCSocket;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.netty.NetworkUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class WebSocketCore extends ListenerAdapter implements HookRetriever, Accessor {
	private static TextChannel cosmeticChannelText;
	private static TextChannel ircChannelText;
	private static String botID;
	public static JDA jda;

	protected static String lastMessage = "";

	public void init() {
	    if (!NetworkUtil.checkNet()) return;
	    
	    if (jda == null || jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.FAILED_TO_LOGIN) {
	        jda = JDABuilder.createDefault(token)
	        		.enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new WebSocketCore()).build();
	    }
	}

	@Override
	public void onReady(ReadyEvent event) {
		cosmeticChannelText = event.getJDA().getTextChannelById(String.valueOf(cosmeticChannel));
		ircChannelText = event.getJDA().getTextChannelById(String.valueOf(ircChannel));
		botID = event.getJDA().getSelfUser().getId();

	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
	    IRCSocket.ircHandler(event);
	    CosmeticSocket.cosmeticListener(event);
	}

	public static TextChannel getCosmeticChannel() {
		return cosmeticChannelText;
	}

	public static String getBotID() {
		return botID;
	}

	public static TextChannel getIrcChannel() {
		return ircChannelText;
	}
}
