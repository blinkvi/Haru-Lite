package cc.unknown.socket.util.webhook.impl;

public class FieldEmbed {

	public String name;
	public String value;
	public Boolean inline;

    public FieldEmbed(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public FieldEmbed(String name, String value, Boolean inline) {
        this(name, value);
        this.inline = inline;
    }

}