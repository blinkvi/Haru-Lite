package cc.unknown.event.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatGUIEvent extends Event {
	public ScaledResolution sr;
    public int x, y;
    
	public ChatGUIEvent(ScaledResolution sr, int x, int y) {
		this.sr = sr;
		this.x = x;
		this.y = y;
	}
}