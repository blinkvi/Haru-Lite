package cc.unknown.handlers;
import cc.unknown.event.player.InboundEvent;
import cc.unknown.util.Accessor;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FixHandler implements Accessor {

	@SubscribeEvent
	public void onServer(InboundEvent event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof S2APacketParticles) {
        	final S2APacketParticles wrapper = ((S2APacketParticles) packet);
        	
        	final double distance = mc.thePlayer.getDistanceSq(wrapper.getXCoordinate(), wrapper.getYCoordinate(), wrapper.getZCoordinate());
        	
        	if (distance >= 26) {
        		event.setCanceled(true);
	        }
        }
    }
}
