package cc.unknown.event.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PreVelocityEvent extends Event {
	public double x, y, z;

	public PreVelocityEvent(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

