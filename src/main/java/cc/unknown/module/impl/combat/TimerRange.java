package cc.unknown.module.impl.combat;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "TimerRange", description = "", category = Category.COMBAT)
public class TimerRange extends Module {
		

    @Override
    public void onDisable() {

    }

	@SubscribeEvent
	public void onAttack(AttackEvent event) {

    }
	
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
    	if (mc.thePlayer == null) return;


	}

	private boolean isTargetNearby(double dist) {
		return mc.theWorld.playerEntities.stream().filter(target -> target != mc.thePlayer).anyMatch(target -> new Vec3(target.posX, target.posY, target.posZ).distanceTo(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) < dist);
	}

}
