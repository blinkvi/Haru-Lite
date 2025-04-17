package cc.unknown.module.impl.move;

import cc.unknown.event.player.PreAttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoJumpDelay", description = "Removes the delay between consecutive jumps, allowing the player to jump rapidly.", category = Category.MOVE)
public class NoJumpDelay extends Module {
		
    @SubscribeEvent
    public void onPreAttack(PreAttackEvent event) {
		if (!isInGame()) return;
		ReflectUtil.setJumpTicks(0);
	};
}