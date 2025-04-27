package cc.unknown.module.impl.visual;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.InboundEvent;
import cc.unknown.event.impl.PrePositionEvent;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

@ModuleInfo(name = "Ambience", description = "Change the time.", category = Category.VISUAL)
public class Ambience extends Module {

	private final SliderValue time = new SliderValue("TimeChanger", this, 0, 0, 1, 0.01f);
	
	@Override
	public void onDisable() {
		clear();
	}
	
    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
		if (!PlayerUtil.isInGame()) return;
		mc.theWorld.setWorldTime((long) (time.getValue() * 22999));
	};

    @EventLink
    public final Listener<PrePositionEvent> onPrePosition = event -> clear();
	
	@EventLink
	public final Listener<InboundEvent> onInbound = event -> {
		Packet<?> packet = event.packet;
		
		if (packet instanceof S03PacketTimeUpdate) {
			event.setCanceled(true);
		} else if (packet instanceof S2BPacketChangeGameState) {
			S2BPacketChangeGameState wrapped = (S2BPacketChangeGameState) packet;
			if (wrapped.getGameState() == 1 || wrapped.getGameState() == 2) {
				event.setCanceled(true);
			}
		}
	};
	
	private void clear() {
		if (!PlayerUtil.isInGame()) return;
		mc.theWorld.setRainStrength(0);
		mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
		mc.theWorld.getWorldInfo().setRainTime(0);
		mc.theWorld.getWorldInfo().setThunderTime(0);
		mc.theWorld.getWorldInfo().setRaining(false);
		mc.theWorld.getWorldInfo().setThundering(false);
	}
}