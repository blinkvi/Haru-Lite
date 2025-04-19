package cc.unknown.module.impl.combat;
import cc.unknown.event.player.AttackEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.player.move.MoveUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "MoreKB", description = "Amplifies knockback effect on opponents during combat.", category = Category.COMBAT)
public class MoreKB extends Module {

    private EntityLivingBase target = null;
    
	@SubscribeEvent
	public void onAttack(AttackEvent event) {
		if (event.getTarget() instanceof EntityPlayer)
			target = (EntityLivingBase) event.getTarget();
	}
	
    @SubscribeEvent
    public void onPreAttack(PrePositionEvent event) {
		if (mc.currentScreen != null || !mc.inGameHasFocus) return;
		if (mc.thePlayer == target) return;
		
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mc.objectMouseOver.entityHit;
        }
        
        if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
            if (mc.thePlayer.hurtTime != 9) {
                mc.thePlayer.sprintingTicksLeft = (int) MathUtil.randomizeSafeInt(0, 10);
            } else {
                mc.thePlayer.sprintingTicksLeft = 0;
            }
        }
    }
}
