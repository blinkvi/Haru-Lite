package cc.unknown.handlers;

import org.lwjgl.opengl.Display;

import cc.unknown.event.render.ChatGUIEvent;
import cc.unknown.event.render.Render2DEvent;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.Accessor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DragHandler implements Accessor {
	
	@SubscribeEvent
	public void onRender2D(Render2DEvent event) {
		if (!isDisplay()) return;
	    
	    for (Drag widget : getDragManager().getDragList()) {
	        if (widget.shouldRender()) {
	            widget.updatePos(event.resolution);
	            widget.render(event.resolution);
	        }
	    }
	}

	@SubscribeEvent
	public void onChatGui(ChatGUIEvent event) {
	    if (!isDisplay()) return;
        
        Drag draggingWidget = null;

        for (Drag widget : getDragManager().getDragList()) {
            if (widget.shouldRender()) {
                widget.onChatGUI(event.mouseX, event.mouseY, (draggingWidget == null || draggingWidget == widget), event.scaledResolution);
                if (widget.dragging) draggingWidget = widget;
            }
            
        	if (widget.shouldRender() && widget.dragging) {
                draggingWidget = widget;
                break;
            }
        }
	}
	
	private boolean isDisplay() {
		if (!Display.isActive() || !Display.isVisible() || !Display.isCreated()) return false;
		if (getModule(ClickGUI.class).pref.isEnabled("HideElementsInGui") && mc.currentScreen == getDropGui()) return false;
		return true;
	}
}
