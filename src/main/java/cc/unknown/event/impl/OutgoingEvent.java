package cc.unknown.event.impl;
import cc.unknown.event.CancellableEvent;
import net.minecraft.network.Packet;

public class OutgoingEvent extends CancellableEvent {
	public Packet<?> packet;

	public OutgoingEvent(Packet<?> packet) {
		this.packet = packet;
	}
}
