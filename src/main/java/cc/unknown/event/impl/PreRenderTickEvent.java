package cc.unknown.event.impl;

import cc.unknown.event.Event;

public class PreRenderTickEvent implements Event {
	public final float renderTickTime;

	public PreRenderTickEvent(float renderTickTime) {
		this.renderTickTime = renderTickTime;
	}
}
