package cc.unknown.handlers;

import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.GameLoopEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShaderHandler {
    public static final CopyOnWriteArrayList<Runnable> tasks = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public void onLoop(GameLoopEvent event) {
        tasks.forEach(Runnable::run);
        tasks.removeIf(task -> true);
    }
}
