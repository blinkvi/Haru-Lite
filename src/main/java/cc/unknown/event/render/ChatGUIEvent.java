package cc.unknown.event.render;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Event;

@AllArgsConstructor
public class ChatGUIEvent extends Event {
	public ScaledResolution scaledResolution;
    public int mouseX, mouseY;
}