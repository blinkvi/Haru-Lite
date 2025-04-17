package cc.unknown.event.render;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderTextEvent extends Event {
	private String string;

	public RenderTextEvent(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}