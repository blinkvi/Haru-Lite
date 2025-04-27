package cc.unknown.command;

import static cc.unknown.util.render.client.ColorUtil.gold;
import static cc.unknown.util.render.client.ColorUtil.green;
import static cc.unknown.util.render.client.ColorUtil.pink;
import static cc.unknown.util.render.client.ColorUtil.red;
import static cc.unknown.util.render.client.ColorUtil.reset;

import java.util.Objects;

import cc.unknown.util.Managers;
import cc.unknown.util.render.client.ChatUtil;

public abstract class Command implements Managers {

    private final ComInfo comInfo;
    private final String name;
    private final String description;

    protected Command() {
        this.comInfo = this.getClass().getAnnotation(ComInfo.class);
        Objects.requireNonNull(comInfo, "ModuleInfo annotation is missing on " + getClass().getName());
        this.name = comInfo.name();
        this.description = comInfo.description();
    }
    
	public abstract void execute(String[] args);

    public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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