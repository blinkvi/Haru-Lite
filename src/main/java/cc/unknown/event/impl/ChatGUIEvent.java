package cc.unknown.event.impl;

import cc.unknown.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class ChatGUIEvent implements Event {
	public ScaledResolution scaledResolution;
    public int mouseX, mouseY;
    
	public ChatGUIEvent(ScaledResolution scaledResolution, int mouseX, int mouseY) {
		this.scaledResolution = scaledResolution;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
}