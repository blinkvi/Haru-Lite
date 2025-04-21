package cc.unknown.event.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatGUIEvent extends Event {
	public ScaledResolution scaledResolution;
    public int mouseX, mouseY;
	public ChatGUIEvent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
		this.scaledResolution = scaledResolution;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
}