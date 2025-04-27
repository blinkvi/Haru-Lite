package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;
import net.minecraft.entity.EntityLivingBase;

public final class AttackEvent extends CancellableEvent {
	public EntityLivingBase target;

	public AttackEvent(EntityLivingBase target) {
		this.target = target;
	}
}
