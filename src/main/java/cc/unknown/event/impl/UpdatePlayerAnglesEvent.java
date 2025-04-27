package cc.unknown.event.impl;
import cc.unknown.event.CancellableEvent;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

public final class UpdatePlayerAnglesEvent extends CancellableEvent {
    public final EntityPlayer entityPlayer;
    public final ModelBiped modelBiped;
    
	public UpdatePlayerAnglesEvent(EntityPlayer entityPlayer, ModelBiped modelBiped) {
		this.entityPlayer = entityPlayer;
		this.modelBiped = modelBiped;
	}
}
