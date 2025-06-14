package cc.unknown.module.impl.combat;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.player.PostPositionEvent;
import cc.unknown.event.player.StrafeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", description = "Modifies the knockback you get.", category = Category.COMBAT)
public class Velocity extends Module {
    private final Mode mode = new Mode("Mode", this, "Normal", "Legit", "Normal");
    
    private final Mode jumpMode = new Mode("JumpMode", this, () -> mode.is("Legit"), "Normal", "Normal", "Hits", "Ticks", "Input");
    
    private final Slider minTicks = new Slider("MinTicks", this, 1, 0, 20, () -> mode.is("Ticks") && mode.is("Legit"));
    private final Slider maxTicks = new Slider("MaxTicks", this, 2, 0, 20, () -> mode.is("Ticks") && mode.is("Legit"));
        
    private final Slider minHits = new Slider("MinHits", this, 2, 0, 20, () -> mode.is("Hits") && mode.is("Legit"));
    private final Slider maxHits = new Slider("MaxHits", this, 2, 0, 20, () -> mode.is("Hits") && mode.is("Legit"));
    
    private final Slider horizontal = new Slider("Horizontal", this, 90, -100, 100, () -> mode.is("Normal"));
    private final Slider vertical = new Slider("Vertical", this, 100, -100, 100, () -> mode.is("Normal"));

    private final Slider chance = new Slider("Chance", this, 100, 0, 100, 1);
    
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("LiquidCheck", true),
			new Bool("OnlyTarget", true),
			new Bool("OnlyMove", true),
			new Bool("DisableOnPressS", true)));
	
	private int hit = 0, tick = 0;
	private boolean reset;
	
	@Override
	public void onEnable() {
		correctValues(minTicks, maxTicks);
		correctValues(minHits, maxHits);
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent ev) {
		if (isInGame() && mc.thePlayer.maxHurtTime > 0 && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {

			if (!shouldApplyVelocity()) return;
			
			if (MathUtil.chance(chance.getValue())) { 
			
				switch (mode.getMode()) {
				case "Normal":
					double horizontal = this.horizontal.getValue();
					double vertical = this.vertical.getValue();
					assert horizontal != 100 || vertical != 100;
					
					mc.thePlayer.motionX *= horizontal / 100;
					mc.thePlayer.motionY *= vertical / 100;
					mc.thePlayer.motionZ *= horizontal / 100;
					break;
				case "Legit":
					switch (jumpMode.getMode()) {
					case "Normal":
						mc.thePlayer.setJumping(mc.thePlayer.onGround);
						break;
					case "Input":
						ReflectUtil.setPressed(mc.gameSettings.keyBindSprint, true);
						ReflectUtil.setPressed(mc.gameSettings.keyBindForward, true);
						ReflectUtil.setPressed(mc.gameSettings.keyBindJump, true);
						ReflectUtil.setPressed(mc.gameSettings.keyBindBack, false);
						reset = true;
						break;
					case "Ticks":
					case "Hits":
					      double direction = Math.atan2(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
					      double degreePlayer = MoveUtil.direction();
					      double degreePacket = Math.floorMod((int)Math.toDegrees(direction), 360);
					      double angle = Math.abs(degreePacket + degreePlayer);
					      double threshold = 120.0D;
					      angle = Math.floorMod((int)angle, 360);
					      boolean inRange = (angle >= 180.0D - threshold / 2.0D && angle <= 180.0D + threshold / 2.0D);
					      if (inRange)
					        reset = true; 
						break;
					}
					
					break;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPostPosition(PostPositionEvent event) {
		if (!shouldApplyVelocity()) return;
		
		if (MathUtil.chance(chance.getValue())) { 
			if (jumpMode.is("Input") && reset) {
				resetKeys(mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindBack);
			} 
			reset = false;
		}
	}
	
	
	@SubscribeEvent
	public void onStrafe(StrafeEvent event) {
	    if (!isInGame()) return;
	    if (!shouldApplyVelocity()) return;

	    if (MathUtil.chance(chance.getValue())) {
	        switch (jumpMode.getMode()) {
	            case "Ticks":
	                tick++;
	                break;
	            case "Hits":
	                if (mc.thePlayer.hurtTime == 9)
	                    hit++;
	                break;
	        }

	        if (reset && !ReflectUtil.isPressed(mc.gameSettings.keyBindJump) && shouldJump() && mc.thePlayer.isSprinting() && mc.thePlayer.hurtTime == 9 && ((conditionals.isEnabled("OnlyTarget") && mc.gameSettings.keyBindAttack.isKeyDown()) || mc.thePlayer.onGround)) {

	            ReflectUtil.setPressed(mc.gameSettings.keyBindJump, true);
	            hit = 0;
	            tick = 0;
	        }

	        reset = false;
	    }
	}
	
	private boolean shouldJump() {
		switch (mode.getMode()) {
		case "Ticks":
			return (tick >= MathUtil.randomInt(minTicks.getAsInt(), (int) (maxTicks.getAsInt() + 0.1D)));
		case "Hits":
			return (hit >= MathUtil.randomInt(minHits.getAsInt(), (int) (maxHits.getAsInt() + 0.1D)));
		} 
		return false;
	}

    private boolean shouldApplyVelocity() {
    	if (!isInGame()) return false;
        if (isEnabled(NoClip.class)) return false;
        if (conditionals.isEnabled("OnlyTarget") && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return false;
        if (conditionals.isEnabled("OnlyMove") && !MoveUtil.isMoving()) return false;
        if (conditionals.isEnabled("LiquidCheck") && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) return false;
        if (conditionals.isEnabled("DisableOnPressS") && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) return false;
        return true;
    }
    
    public void resetKeys(KeyBinding... keys) {
        boolean inGui = mc.currentScreen != null;
        for (KeyBinding key : keys) {
            boolean shouldPress = !inGui && Keyboard.isKeyDown(key.getKeyCode());
            ReflectUtil.setPressed(key, shouldPress);
        }
    }
}