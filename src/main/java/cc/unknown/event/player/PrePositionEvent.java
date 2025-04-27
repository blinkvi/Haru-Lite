package cc.unknown.event.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PrePositionEvent extends Event {
	public double x, y, z;
    public float yaw, pitch;
    public boolean onGround, isSprinting, isSneaking;
    
	public PrePositionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean isSprinting, boolean isSneaking) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.isSprinting = isSprinting;
		this.isSneaking = isSneaking;
	}
}