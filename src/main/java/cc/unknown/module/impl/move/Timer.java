package cc.unknown.module.impl.move;

import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.Slider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Timer", description = "Faster game.", category = Category.MOVE)
public class Timer extends Module {
	
	private final Mode mode = new Mode("Mode", this, "Vanilla", "Vanilla", "Ground", "Air", "Ground/Air", "Random");
	
	private final Slider speed = new Slider("Speed", this, 1.5, 0.05, 25, 0.01);
	private final Slider variation = new Slider("Randomization", this, 15, 5, 50, 5, () -> mode.is("Random"));
	
	private final Slider ground = new Slider("GroundSpeed", this, 0.04, 0.05, 25, 0.01, () -> mode.is("Ground/Air"));
	private final Slider air = new Slider("AirSpeed", this, 2, 0.05, 25, 0.01, () -> mode.is("Ground/Air"));
	
	@Override
	public void onDisable() {
		if (!isInGame()) return;
		ReflectUtil.getTimer().timerSpeed = 1.0f;
	}

	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (!isInGame()) return;
		
		switch (mode.getMode()) {
		case "Vanilla":
			ReflectUtil.getTimer().timerSpeed = speed.getAsFloat();
			break;
		case "Ground":
			if (mc.thePlayer.onGround) 
				ReflectUtil.getTimer().timerSpeed = speed.getAsFloat();
			break;
		case "Air":
			if (!mc.thePlayer.onGround)
				ReflectUtil.getTimer().timerSpeed = speed.getAsFloat();
			break;
		case "Ground/Air":
			if (mc.thePlayer.onGround) 
				ReflectUtil.getTimer().timerSpeed = ground.getAsFloat();
			
			if (!mc.thePlayer.onGround)
				ReflectUtil.getTimer().timerSpeed = air.getAsFloat();
				break;
		case "Random":
			float randomization = variation.getAsInt();
		    float halfVariation = randomization / 2.0F;
		    float randomOffset = ThreadLocalRandom.current().nextFloat() * halfVariation * 2 - halfVariation;

		    ReflectUtil.getTimer().timerSpeed = Math.max(speed.getAsFloat() + randomOffset, 0.1F);
			break;
		}
	}
}