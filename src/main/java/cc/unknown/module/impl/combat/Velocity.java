package cc.unknown.module.impl.combat;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.event.player.PrePositionEvent;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", description = "Modifies the knockback you get.", category = Category.COMBAT)
public class Velocity extends Module {
    private final Mode mode = new Mode("Mode", this, "Normal", "Legit", "Normal", "Intave");
    
    private final Mode jumpMode = new Mode("JumpMode", this, () -> mode.is("Legit"), "Normal", "Normal", "Hits", "Ticks");
    
    private final Slider minTicks = new Slider("MinTicks", this, 1, 0, 20, () -> jumpMode.is("Ticks") && mode.is("Legit"));
    private final Slider maxTicks = new Slider("MaxTicks", this, 2, 0, 20, () -> jumpMode.is("Ticks") && mode.is("Legit"));
        
    private final Slider minHits = new Slider("MinHits", this, 2, 0, 20, () -> jumpMode.is("Hits") && mode.is("Legit"));
    private final Slider maxHits = new Slider("MaxHits", this, 2, 0, 20, () -> jumpMode.is("Hits") && mode.is("Legit"));
    
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
	private boolean attacked;
	
	@Override
	public void guiUpdate() {
		correct(minTicks, maxTicks);
		correct(minHits, maxHits);
	}
	
	@SubscribeEvent
	public void onAttack(AttackEvent event) {
		if (mode.is("Intave")) {
			 attacked = true;
		}
	}

    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
    	if (mc.thePlayer.hurtTime > 0) {
			switch (mode.getMode()) {
			case "Intave":
		        if (mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && mc.thePlayer.hurtTime > 0 && !attacked) {
		            mc.thePlayer.motionX *= 0.6D;
		            mc.thePlayer.motionZ *= 0.6D;
		            mc.thePlayer.setSprinting(false);
		        }

		        attacked = false;
				break;
			case "Normal":
				if (horizontal.getAsInt() > 0 || horizontal.getAsInt() < 0 && vertical.getAsInt() > 0 || vertical.getAsInt() < 0) {
					mc.thePlayer.motionX *= horizontal.getAsInt() / 100;
					mc.thePlayer.motionY *= vertical.getAsInt() / 100;
					mc.thePlayer.motionZ *= horizontal.getAsInt() / 100;
				}

				break;
			case "Legit":
				switch (jumpMode.getMode()) {
				case "Normal":
					mc.thePlayer.setJumping(mc.thePlayer.onGround);
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

	@SubscribeEvent
	public void onStrafe(StrafeEvent event) {
	    if (!isInGame()) return;
		if (!shouldApplyVelocity()) return;
		if (!MathUtil.chance(chance.getValue())) return;

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
}