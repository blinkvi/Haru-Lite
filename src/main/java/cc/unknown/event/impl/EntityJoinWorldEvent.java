package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityJoinWorldEvent extends CancellableEvent {
    public final Entity entity;
    public final World world;

    public EntityJoinWorldEvent(Entity entity, World world) {
        this.entity = entity;
        this.world = world;
    }
}