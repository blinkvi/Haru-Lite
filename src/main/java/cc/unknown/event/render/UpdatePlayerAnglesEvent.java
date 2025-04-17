package cc.unknown.event.render;
import lombok.AllArgsConstructor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
public final class UpdatePlayerAnglesEvent extends Event {
    public final EntityPlayer entityPlayer;
    public final ModelBiped modelBiped;
}
