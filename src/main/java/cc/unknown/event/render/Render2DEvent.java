package cc.unknown.event.render;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

@AllArgsConstructor
public class Render2DEvent extends Event {
    public final ScaledResolution resolution;
    public final float partialTicks;
    
    public int getScaledWidth() {
    	return resolution.getScaledWidth();
    }
    
    public int getScaledHeight() {
    	return resolution.getScaledHeight();
    }
}
