package cc.unknown.module.impl.combat;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.player.move.MoveUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "MoreKB", description = "Amplifies knockback effect on opponents during combat.", category = Category.COMBAT)
public class MoreKB extends Module {
	
	@SubscribeEvent
	public void onAttack(AttackEvent event) {
		if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
            if (mc.thePlayer.hurtTime != 9) {
                mc.thePlayer.sprintingTicksLeft = (int) MathUtil.randomizeSafeInt(0, 10);
            } else {
                mc.thePlayer.sprintingTicksLeft = 0;
            }
        }
	}
}
