package cc.unknown.socket.util.webhook.impl;

public class AuthorEmbed {

	public String name;
    public String iconurl;
    public String url;

    public AuthorEmbed(String name, String iconurl) {
        this.name = name;
        this.iconurl = iconurl;
    }

    public AuthorEmbed(String name, String iconurl, String url) {
        this(name, iconurl);
        this.url = url;
    }

}
