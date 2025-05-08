package cc.unknown.socket.util.webhook;

import java.io.File;
import java.util.List;

public final class DiscordMessage {

	public String username;
	public String avatar_url;
	public String content;
	public Boolean tts;
	public List<DiscordEmbed> embeds;
	public transient List<File> files;

	public DiscordMessage() {}

	public static Builder builder() {
		return new Builder();
	}
}
