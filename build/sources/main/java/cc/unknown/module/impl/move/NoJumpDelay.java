package cc.unknown.module.impl.move;

import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoJumpDelay", description = "Remove jump delay.", category = Category.MOVE)
public class NoJumpDelay extends Module {

    @SubscribeEvent
    public void onPreAttack(PreUpdateEvent event) {
        if (!isInGame()) return;

        ReflectUtil.setJumpTicks(0);
    }
}