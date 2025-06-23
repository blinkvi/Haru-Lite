package cc.unknown.module.impl.move;

import cc.unknown.event.player.PreMoveInputEvent;
import cc.unknown.event.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Speed", description = "", category = Category.MOVE)
public class Speed extends Module {

    private final Slider speedInc = new Slider("Speed", this, 1.12, 1, 1.4, 0.1);
    private final Bool speed = new Bool("IncreaseSpeed", this, false);
    private final Bool legitStrafe = new Bool("LegitStrafe", this, false);
    private final Bool fastFall = new Bool("FastFall", this, false);
    
	@Override
	public void onEnable() {
		if (mc.thePlayer == null) return;
	}
	
    @SubscribeEvent
    public void onMoveInput(PreMoveInputEvent event) {
		if (isEnabled(LegitScaffold.class)) return;
		
	    if (event.forward > 0.8) {
	        mc.thePlayer.setSprinting(true);
	    }

	    if (shouldJump()) {
	        event.jump = true;
	    }
	}
	
	@SubscribeEvent
	public void onStrafe(StrafeEvent event) {
        if(speed.get()) {
            event.friction = event.friction * speedInc.getAsFloat();
        }
        
        if(legitStrafe.get() && !mc.thePlayer.onGround && (event.strafe != 0 || event.forward != 0)) {
            event.yaw = MoveUtil.getStrafeYaw(event.forward, event.strafe);
            event.forward = 1;
            event.strafe = 0;
        }
        
        if(fastFall.get()) {
            if(mc.thePlayer.fallDistance > 1.5) {
                mc.thePlayer.motionY *= 1.075;
            }
        }
	}
	
	private boolean shouldJump() {
	    return (mc.thePlayer.onGround && MoveUtil.isMoving()) || mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
	}

}