package cc.unknown.socket.util.webhook;

import java.util.List;

import cc.unknown.socket.util.webhook.impl.*;

public class DiscordEmbed {

	String title;
	String description;
	String url;
	Integer color;
	String timestamp;
	AuthorEmbed author;
	ImageEmbed image;
	ThumbnailEmbed thumbnail;
	FooterEmbed footer;
	List<FieldEmbed> fields;
	
	public DiscordEmbed() {}

	public static Builder builder() {
		return new Builder();
	}
}
