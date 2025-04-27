package cc.unknown.event.render;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderTextEvent extends Event {
	public String string;

	public RenderTextEvent(String string) {
		this.string = string;
	}
}