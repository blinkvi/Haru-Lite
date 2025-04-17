package cc.unknown.handlers;

import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.GameEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShaderHandler {
    public final static CopyOnWriteArrayList<Runnable> tasks = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public void onLoop(GameEvent event) {
        for (Runnable task : tasks) {
            task.run();
            tasks.remove(task);
        }
    }
}
