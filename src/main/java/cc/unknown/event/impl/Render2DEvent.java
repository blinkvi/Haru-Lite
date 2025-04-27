package cc.unknown.event.impl;
import cc.unknown.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent implements Event {
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
