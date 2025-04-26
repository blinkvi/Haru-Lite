package cc.unknown.event.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MoveInputEvent extends Event {
	public float forward, strafe;
	public boolean jump, sneak;
	public double sneakSlowDownMultiplier;

	public MoveInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakSlowDownMultiplier) {
		this.forward = forward;
		this.strafe = strafe;
		this.jump = jump;
		this.sneak = sneak;
		this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
	}
}
