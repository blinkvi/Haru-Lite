package cc.unknown.module.impl.combat;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.util.render.client.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

@ModuleInfo(name = "MoreKB", description = "Amplifies knockback effect on opponents during combat.", category = Category.COMBAT)
public class MoreKB extends Module {
	
	@EventLink
	public final Listener<PrePositionEvent> onPrePosition = event -> {
		if (!PlayerUtil.isInGame()) return;

		if (mc.currentScreen != null || !mc.inGameHasFocus) return;
		
		if (mc.objectMouseOver != null 
			    && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY 
			    && mc.objectMouseOver.entityHit instanceof EntityPlayer
			    && mc.thePlayer.isSwingInProgress) {
			if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                if (mc.thePlayer.hurtTime != 9) {
                    mc.thePlayer.sprintingTicksLeft = (int) MathUtil.randomizeSafeInt(0, 10);
                    ChatUtil.display("ResetTicks: " + MathUtil.randomizeSafeInt(0, 10));
                } else {
                    mc.thePlayer.sprintingTicksLeft = 0;
                    ChatUtil.display("ResetSprint");
                }
            }
        }
	};
}
