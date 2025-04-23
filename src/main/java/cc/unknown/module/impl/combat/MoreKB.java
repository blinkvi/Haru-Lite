package cc.unknown.module.impl.combat;
import cc.unknown.event.player.AttackEvent;
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

    @SubscribeEvent
    public void onAttack(AttackEvent event) {
        if (!shouldTrigger(event)) return;

        if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
            boolean justHit = mc.thePlayer.hurtTime == 9;
            mc.thePlayer.sprintingTicksLeft = justHit ? 0 : (int) MathUtil.randomizeSafeInt(0, 10);
        }
    }

    private boolean shouldTrigger(AttackEvent event) {
        if (mc.currentScreen != null || !mc.inGameHasFocus) return false;
        if (!(event.target instanceof EntityPlayer)) return false;

        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase;
    }
}
