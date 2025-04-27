package cc.unknown.event.impl;
import cc.unknown.event.CancellableEvent;
import net.minecraft.network.Packet;

public class InboundEvent extends CancellableEvent {
	public Packet<?> packet;

	public InboundEvent(Packet<?> packet) {
		this.packet = packet;
	}
}
