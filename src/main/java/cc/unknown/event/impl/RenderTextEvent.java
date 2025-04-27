package cc.unknown.event.impl;

import cc.unknown.event.Event;

public class RenderTextEvent implements Event {
	public String string;

	public RenderTextEvent(String string) {
		this.string = string;
	}
}