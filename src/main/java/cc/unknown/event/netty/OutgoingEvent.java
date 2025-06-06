package cc.unknown.event.netty;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class OutgoingEvent extends Event {
	public Packet packet;

	public OutgoingEvent(Packet packet) {
		this.packet = packet;
	}
}
