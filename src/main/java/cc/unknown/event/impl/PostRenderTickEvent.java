package cc.unknown.event.impl;

import cc.unknown.event.Event;

public class PostRenderTickEvent implements Event {
	public final float renderTickTime;

	public PostRenderTickEvent(float renderTickTime) {
		this.renderTickTime = renderTickTime;
	}
}
