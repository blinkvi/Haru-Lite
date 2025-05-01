package cc.unknown.command;

import static cc.unknown.util.render.client.ColorUtil.gold;
import static cc.unknown.util.render.client.ColorUtil.green;
import static cc.unknown.util.render.client.ColorUtil.pink;
import static cc.unknown.util.render.client.ColorUtil.red;
import static cc.unknown.util.render.client.ColorUtil.reset;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.client.ChatUtil;

public abstract class Command implements Accessor {

    protected final String prefix;

    public Command(String prefix) {
		this.prefix = prefix;
	}

	public String prefix() {
		return prefix;
	}

	public abstract void execute(String[] args);

    public void error(String message) {
        ChatUtil.display(String.format("%s[%s*%s] %s", pink, red, pink, reset + message));
    }

    public void warning(String message) {
        ChatUtil.display(String.format("%s[%s*%s] %s", pink, gold, pink, reset + message));
    }

    public void success(String message) {
        ChatUtil.display(String.format("%s[%s*%s] %s", pink, green, pink, reset + message));
    }
}