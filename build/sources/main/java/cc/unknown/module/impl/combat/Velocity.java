package cc.unknown.module.impl.combat;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.netty.InboundEvent;
import cc.unknown.event.player.PostVelocityEvent;
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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", description = "Modifies the knockback you get.", category = Category.COMBAT)
public class Velocity extends Module {
    private final Mode mode = new Mode("Mode", this, "Normal", "Jump", "Normal");
    private final Slider horizontal = new Slider("Horizontal", this, 0.9f, 0, 1, 0.1f, () -> mode.is("Normal"));
    private final Slider vertical = new Slider("Vertical", this, 1, 0, 1, 0.1f, () -> mode.is("Normal"));
    private final Slider chance = new Slider("Chance", this, 100, 0, 100, 1);
    
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("LiquidCheck", true),
			new Bool("OnlyTarget", true),
			new Bool("OnlyMove", true),
			new Bool("DisableOnPressS", true)));

    @SubscribeEvent
    public void onPostVelocity(PostVelocityEvent event) {
    	if (!shouldApplyVelocity()) return;
    	if (!MathUtil.chance(chance.getValue())) return;
    	
        if (mode.is("Jump")) {
        	mc.thePlayer.setJumping(mc.thePlayer.onGround);
        }
    };
    
    @SubscribeEvent
    public void onInbound(InboundEvent event) {
    	if (!shouldApplyVelocity()) return;
    	if (!MathUtil.chance(chance.getValue())) return;
    	
    	if (mode.is("Normal")) {
	        if (event.isCanceled()) return;
	
	        Packet<?> packet = event.packet;
	
	        if (packet instanceof S12PacketEntityVelocity) {
	            S12PacketEntityVelocity velocity = (S12PacketEntityVelocity) packet;
	
	            if (velocity.getEntityID() != mc.thePlayer.getEntityId()) return;
	
	            double horizontalScale = horizontal.getValue();
	            double verticalScale = vertical.getValue();
	
	            if (horizontalScale == 0) {
	                if (verticalScale != 0) {
	                    mc.thePlayer.motionY = velocity.getMotionY() / 8000.0D * verticalScale;
	                }
	
	                event.setCanceled(true);
	                return;
	            }
	
	            int motionX = (int) (velocity.getMotionX() * horizontalScale);
	            int motionY = (int) (velocity.getMotionY() * verticalScale);
	            int motionZ = (int) (velocity.getMotionZ() * horizontalScale);
	
	            ReflectUtil.setMotionX(velocity, motionX);
	            ReflectUtil.setMotionY(velocity, motionY);
	            ReflectUtil.setMotionZ(velocity, motionZ);
	
	            event.packet = velocity;
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