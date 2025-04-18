package cc.unknown.module.impl.visual;

import cc.unknown.event.PreTickEvent;
import cc.unknown.event.player.InboundEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("rawtypes")
@ModuleInfo(name = "Ambience", description = "Change the time.", category = Category.VISUAL)
public class Ambience extends Module {

	private final SliderValue time = new SliderValue("Time", this, 0, 0, 1, 0.01f);
	
	@Override
	public void onDisable() {
		clear();
	}
	
	@SubscribeEvent
	public void onRender3D(PreTickEvent event) {
		if (!isInGame()) return;
		mc.theWorld.setWorldTime((long) (time.getValue() * 22999));
	}

	@SubscribeEvent
	public void onPreAttack(PrePositionEvent event) {
	    clear();
	}

	@SubscribeEvent
	public void onInbound(InboundEvent event) {
		Packet packet = event.getPacket();
		
		if (packet instanceof S03PacketTimeUpdate) {
			event.setCanceled(true);
		} else if (packet instanceof S2BPacketChangeGameState) {
			S2BPacketChangeGameState wrapped = (S2BPacketChangeGameState) packet;
			if (wrapped.getGameState() == 1 || wrapped.getGameState() == 2) {
				event.setCanceled(true);
			}
		}
	}
	
	private void clear() {
		if (!isInGame()) return;
		mc.theWorld.setRainStrength(0);
		mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.theWorld.getWorldInfo().setRainTime(0);
		mc.theWorld.getWorldInfo().setThunderTime(0);
		mc.theWorld.getWorldInfo().setRaining(false);
		mc.theWorld.getWorldInfo().setThundering(false);
	}
}