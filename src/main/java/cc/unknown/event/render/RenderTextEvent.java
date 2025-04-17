package cc.unknown.event.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Event;

@AllArgsConstructor
@Getter
@Setter
public class RenderTextEvent extends Event {
	private String string;
}