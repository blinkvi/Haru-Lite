package cc.unknown.module.impl.combat;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.value.impl.Slider;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Criticals", description = "", category = Category.COMBAT)
public class Criticals extends Module {
		
	private final Slider timer = new Slider("Timer", this, 0.5, 0.1, 1, 0.1);
    private final Slider delay = new Slider("Delay", this, 2000, 100, 3000, 100);
	private final Slider range = new Slider("Range", this, 3, 3, 6, 0.1);
    private final Slider chance = new Slider("Chance", this, 100, 0, 100);
	
    private long startTimer;
    private boolean delayed = false;
    private boolean attacked;
	
    @Override
    public void onDisable() {
    	if (!isInGame()) return;
    	
        if (startTimer != -1) {
            ReflectUtil.getTimer().timerSpeed = 1.0f;
        }
        startTimer = -1;
    }

	@SubscribeEvent
	public void onAttack(AttackEvent event) {
		if (!isInGame()) return;
		if (!MathUtil.chance(chance.getValue())) return;
		
		attacked = true;
    }
	
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
    	if (mc.thePlayer == null) return;
    	if (!isInGame()) return;

    	if (startTimer != -1) {
    		if (mc.thePlayer.onGround || delayed || System.currentTimeMillis() - startTimer > delay.getAsLong()) {
    			ReflectUtil.getTimer().timerSpeed = 1.0f;
    			startTimer = -1;
    			attacked = false;
    		}
    	} else if (mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround && !delayed && attacked) {
    		if (ReflectUtil.getTimer().timerSpeed != timer.getAsFloat() && chance.getValue() != 100 && MathUtil.chance(chance.getValue())) {
    			delayed = true;
    			return;
    		}

    		if (isTargetNearby(range.getValue())) {
    			startTimer = System.currentTimeMillis();
    			ReflectUtil.getTimer().timerSpeed = timer.getAsFloat();
    		}
    	} else if (mc.thePlayer.onGround) {
    		delayed = false;
        }
	}

	private boolean isTargetNearby(double dist) {
		return mc.theWorld.playerEntities.stream().filter(target -> target != mc.thePlayer).anyMatch(target -> new Vec3(target.posX, target.posY, target.posZ).distanceTo(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) < dist);
	}

}
