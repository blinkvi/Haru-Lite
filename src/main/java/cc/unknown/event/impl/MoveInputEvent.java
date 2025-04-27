package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;

public class MoveInputEvent extends CancellableEvent {
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
