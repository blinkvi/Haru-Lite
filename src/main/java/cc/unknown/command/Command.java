package cc.unknown.command;

import static cc.unknown.util.render.client.ColorUtil.*;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.client.ChatUtil;

public abstract class Command implements Accessor {

    protected final String prefix;

    public Command(String prefix) {
        this.prefix = prefix;
    }

    public abstract void execute(String[] args);

    public String getPrefix() {
        return prefix;
    }

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