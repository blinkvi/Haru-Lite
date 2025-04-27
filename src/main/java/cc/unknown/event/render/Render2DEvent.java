package cc.unknown.event.render;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Render2DEvent extends Event {
    public final ScaledResolution resolution;
    public final float partialTicks;
    
    public Render2DEvent(ScaledResolution resolution, float partialTicks) {
		this.resolution = resolution;
		this.partialTicks = partialTicks;
	}

	public int getScaledWidth() {
    	return resolution.getScaledWidth();
    }
    
    public int getScaledHeight() {
    	return resolution.getScaledHeight();
    }
}
