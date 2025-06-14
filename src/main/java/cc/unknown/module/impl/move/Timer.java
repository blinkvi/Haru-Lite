package cc.unknown.module.impl.move;

import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "Timer", description = "Faster game.", category = Category.MOVE)
public class Timer extends Module {
	
	private final Slider speed = new Slider("Speed", this, 1.5, 0.05, 25, 0.05);
	private final Slider variation = new Slider("Randomization", this, 15, 0, 50, 5);
	
	private final Bool ground = new Bool("Ground", this, false);
	private final Slider groundSpeed = new Slider("GroundSpeed", this, 1.5, 0.05, 20, 0.05, ground::get);
	private final Bool air = new Bool("Air", this, false);
	private final Slider airSpeed = new Slider("AirSpeed", this, 1.5, 0.05, 20, 0.05, air::get);
	
	@Override
	public void onEnable() {
		ReflectUtil.getTimer().timerSpeed = 1f;
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase == Phase.START && mc.thePlayer != null) {
	        ReflectUtil.getTimer().timerSpeed = calculateTimerSpeed();
	    }
	}
	
	private float calculateTimerSpeed() {
	    boolean onGround = mc.thePlayer.onGround;
	    float baseSpeed;

	    if (onGround && ground.get()) {
	        baseSpeed = groundSpeed.getAsFloat();
	    } else if (air.get() && !onGround) {
	        baseSpeed = airSpeed.getAsFloat();
	    } else {
	        baseSpeed = speed.getAsFloat();
	    }

	    float variationAmount = variation.getAsFloat();
	    
	    if (variationAmount <= 0) {
	        return Math.max(baseSpeed, 0.1F);
	    }

	    float halfVariation = variationAmount / 2.0F;
	    float randomOffset = ThreadLocalRandom.current().nextFloat() * halfVariation * 2 - halfVariation;

	    return Math.max(baseSpeed + randomOffset, 0.1F);
	}
}