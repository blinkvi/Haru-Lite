package cc.unknown.module.impl.combat;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.player.PostVelocityEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.move.NoClip;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", description = "Modifies the knockback you get.", category = Category.COMBAT)
public class Velocity extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Normal", "Jump", "Normal");
    private final SliderValue chance = new SliderValue("Chance", this, 1, 0, 1, 0.1f);
    private final SliderValue horizontal = new SliderValue("Horizontal", this, 0.9f, 0, 1, 0.1f, () -> mode.is("Normal"));
    private final SliderValue vertical = new SliderValue("Vertical", this, 1, 0, 1, 0.1f, () -> mode.is("Normal"));
    
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("LiquidCheck", true),
			new BoolValue("OnlyTarget", true),
			new BoolValue("OnlyMove", true),
			new BoolValue("DisableOnPressS", true)));

    @SubscribeEvent
    public void onPostVelocity(PostVelocityEvent event) {
    	if (!shouldApplyVelocity()) return;
    	
        if (mode.is("Jump") && !mc.gameSettings.keyBindJump.isKeyDown()) {
        	mc.thePlayer.setJumping(mc.thePlayer.onGround);
        }
    };

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!shouldApplyVelocity()) return;
                
        double horizontal = this.horizontal.getValue() * 100 / 100.0;
        double vertical = this.vertical.getValue() * 100 / 100.0;

        if (mode.is("Normal") && mc.thePlayer.maxHurtTime > 0 && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {
        	assert horizontal != 1 || vertical != 1;
        	mc.thePlayer.motionX *= horizontal;
        	mc.thePlayer.motionZ *= horizontal;
        	mc.thePlayer.motionY *= vertical;
        }
    }

    private boolean shouldApplyVelocity() {
    	if (!isInGame()) return false;
        if (!MathUtil.chanceApply(chance.getValue())) return false;
        if (isEnabled(NoClip.class)) return false;
        if (conditionals.isEnabled("OnlyTarget") && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) return false;
        if (conditionals.isEnabled("OnlyMove") && !MoveUtil.isMoving()) return false;
        if (conditionals.isEnabled("LiquidCheck") && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) return false;
        if (conditionals.isEnabled("DisableOnPressS") && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) return false;
        return true;
    }
}