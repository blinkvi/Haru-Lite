package cc.unknown.event.player;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@SuppressWarnings("rawtypes")
public class OutgoingEvent extends Event {
	public Packet packet;

	public OutgoingEvent(Packet packet) {
		this.packet = packet;
	}
}
