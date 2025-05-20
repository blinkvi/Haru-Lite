package cc.unknown.event.render;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Render2DEvent extends Event {
    public final ScaledResolution sr;
    public final float partialTicks;
    
    public Render2DEvent(ScaledResolution sr, float partialTicks) {
		this.sr = sr;
		this.partialTicks = partialTicks;
	}

	public int width() {
    	return sr.getScaledWidth();
    }
    
    public int height() {
    	return sr.getScaledHeight();
    }
}
