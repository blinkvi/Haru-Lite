package cc.unknown.event.player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
@Getter
@Setter
@SuppressWarnings("rawtypes")
public class InboundEvent extends Event {
	private Packet packet;
}
