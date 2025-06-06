package cc.unknown.module.impl.combat;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", description = "Modifies the knockback you get.", category = Category.COMBAT)
public class Velocity extends Module {
    private final Mode mode = new Mode("Mode", this, "Normal", "Jump", "Normal");
    private final Slider horizontal = new Slider("Horizontal", this, 90, -100, 100, () -> mode.is("Normal"));
    private final Slider vertical = new Slider("Vertical", this, 100, -100, 100, () -> mode.is("Normal"));

    private final Slider chance = new Slider("Chance", this, 100, 0, 100, 1);
    
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("LiquidCheck", true),
			new Bool("OnlyTarget", true),
			new Bool("OnlyMove", true),
			new Bool("DisableOnPressS", true)));
	
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
				case "Jump":
					mc.thePlayer.setJumping(mc.thePlayer.onGround);
					break;
				}
			}
		}
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