package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;

public class SlowDownEvent extends CancellableEvent {
	public float strafeMultiplier;
	public float forwardMultiplier;
	public boolean sprint;

    public SlowDownEvent(float strafeMultiplier, float forwardMultiplier) {
		this.strafeMultiplier = strafeMultiplier;
		this.forwardMultiplier = forwardMultiplier;
	}
}
