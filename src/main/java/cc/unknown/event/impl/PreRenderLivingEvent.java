package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;
import net.minecraft.entity.EntityLivingBase;

public class PreRenderLivingEvent extends CancellableEvent {
    public final EntityLivingBase entity;
    public final double x;
    public final double y;
    public final double z;

    public PreRenderLivingEvent(EntityLivingBase entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
