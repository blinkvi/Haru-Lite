package cc.unknown.event.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class AttackEvent extends Event {
	public EntityLivingBase target;

	public AttackEvent(EntityLivingBase target) {
		this.target = target;
	}
}
