package cc.unknown.event.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public final class AttackEvent extends Event {
    private EntityLivingBase target;

	public AttackEvent(EntityLivingBase target) {
		this.target = target;
	}

	public EntityLivingBase getTarget() {
		return target;
	}

	public void setTarget(EntityLivingBase target) {
		this.target = target;
	}
}
