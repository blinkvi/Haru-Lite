package cc.unknown.event.render;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public final class UpdatePlayerAnglesEvent extends Event {
    public final EntityPlayer player;
    public final ModelBiped model;
    
	public UpdatePlayerAnglesEvent(EntityPlayer player, ModelBiped model) {
		this.player = player;
		this.model = model;
	}
}
