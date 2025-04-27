package cc.unknown.event.impl;

import org.lwjgl.input.Mouse;

import cc.unknown.event.CancellableEvent;

public class MouseEvent extends CancellableEvent {
	public final int x;
	public final int y;
	public final int dx;
	public final int dy;
	public final int dwheel;
	public final int button;
	public final boolean buttonstate;
	public final long nanoseconds;

	public MouseEvent() {
		this.x = Mouse.getEventX();
		this.y = Mouse.getEventY();
		this.dx = Mouse.getEventDX();
		this.dy = Mouse.getEventDY();
		this.dwheel = Mouse.getEventDWheel();
		this.button = Mouse.getEventButton();
		this.buttonstate = Mouse.getEventButtonState();
		this.nanoseconds = Mouse.getEventNanoseconds();
	}
}
