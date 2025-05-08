package cc.unknown.socket.util.webhook.impl;

public class Field {
	public String name;
    public String value;
    public boolean inline;

    public Field(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }
}
