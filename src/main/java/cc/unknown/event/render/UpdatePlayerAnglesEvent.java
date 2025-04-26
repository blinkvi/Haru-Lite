package cc.unknown.event.render;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public final class UpdatePlayerAnglesEvent extends Event {
    public final EntityPlayer entityPlayer;
    public final ModelBiped modelBiped;
    
	public UpdatePlayerAnglesEvent(EntityPlayer entityPlayer, ModelBiped modelBiped) {
		this.entityPlayer = entityPlayer;
		this.modelBiped = modelBiped;
	}
}
