package cc.unknown.event.netty;
import io.netty.channel.Channel;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@SuppressWarnings("rawtypes")
public class InboundEvent extends Event {
	public Packet packet;
	public INetHandler packetListener;
	public Channel channel;
	
	public InboundEvent(Packet packet, INetHandler packetListener, Channel channel) {
		this.packet = packet;
		this.packetListener = packetListener;
		this.channel = channel;
	}
}
