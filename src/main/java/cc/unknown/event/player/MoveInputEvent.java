package cc.unknown.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
@Getter
@Setter
public class MoveInputEvent extends Event {
	private float forward, strafe;
	private boolean jump, sneak;
	private double sneakSlowDownMultiplier;
}
