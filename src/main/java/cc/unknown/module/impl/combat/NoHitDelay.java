package cc.unknown.module.impl.combat;

import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoHitDelay", description = "Remove clicks delay.", category = Category.COMBAT)
public class NoHitDelay extends Module {
	
    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (!isInGame()) return;
        ReflectUtil.setLeftClickCounter(0);
    }
}
