package cc.unknown.socket;
import cc.unknown.Haru;
import cc.unknown.socket.api.HookRetriever;
import cc.unknown.socket.impl.IRCSocket;
import cc.unknown.util.client.network.NetworkUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class WebSocketCore extends ListenerAdapter implements HookRetriever {
	private static TextChannel cosmeticChannelText;
	private static TextChannel ircChannelText;
	private static String botID;
	public static JDA jda;

	protected static String lastMessage = "";

	public void init() {
	    if (!NetworkUtil.checkNet()) return;
	    
	    if (jda == null || jda.getStatus() == JDA.Status.SHUTDOWN || jda.getStatus() == JDA.Status.FAILED_TO_LOGIN || jda.getStatus() == JDA.Status.DISCONNECTED) {
	    	if (Haru.cris) {
		    	jda = JDABuilder.createDefault("MTM2Mjg1NjczMzE1MTg1NDg1NA.GVDlHZ.dSnz1c94wEFWak-SEzW0WlgqyMOFeWagkNKNVw").enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new WebSocketCore()).build();
	    	} else {
	    		jda = JDABuilder.createDefault(NetworkUtil.getRaw("bot", host, "c")).enableIntents(GatewayIntent.MESSAGE_CONTENT).addEventListeners(new WebSocketCore()).build();	
	    	}
	    }
	}

	@Override
	public void onReady(ReadyEvent event) {
		if (Haru.cris) {
			cosmeticChannelText = event.getJDA().getTextChannelById("1360731034278428976");
			ircChannelText = event.getJDA().getTextChannelById("1356750457720143946");
		} else {
			cosmeticChannelText = event.getJDA().getTextChannelById(String.valueOf(NetworkUtil.getRaw("cosme_id", host, "d")));
			ircChannelText = event.getJDA().getTextChannelById(String.valueOf(NetworkUtil.getRaw("irc_id", host, "a")));	
		}
		botID = event.getJDA().getSelfUser().getId();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
	    IRCSocket.ircHandler(event);
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
