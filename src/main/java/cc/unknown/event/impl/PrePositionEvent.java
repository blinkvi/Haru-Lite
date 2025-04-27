package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;

public class PrePositionEvent extends CancellableEvent {
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