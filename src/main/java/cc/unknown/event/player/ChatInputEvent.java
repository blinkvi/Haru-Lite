package cc.unknown.event.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ChatInputEvent extends Event {
	public String message;

	public ChatInputEvent(String message) {
		this.message = message;
	}
}
