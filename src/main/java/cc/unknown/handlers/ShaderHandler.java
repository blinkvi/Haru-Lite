package cc.unknown.handlers;

import java.util.concurrent.CopyOnWriteArrayList;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.GameEvent;

public class ShaderHandler {
    public static final CopyOnWriteArrayList<Runnable> tasks = new CopyOnWriteArrayList<>();

    @EventLink
    public final Listener<GameEvent> onGame = event -> {
        tasks.forEach(Runnable::run);
        tasks.removeIf(task -> true);
    };
}
