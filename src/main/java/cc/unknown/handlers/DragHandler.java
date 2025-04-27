package cc.unknown.handlers;

import java.util.Optional;

import org.lwjgl.opengl.Display;

import cc.unknown.Haru;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.ChatGUIEvent;
import cc.unknown.event.impl.Render2DEvent;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.Managers;
import cc.unknown.util.structure.list.SList;

public class DragHandler implements Managers {
	
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {
		if (!isDisplay()) return;

		Haru.dragMngr.getDragList().stream()
			.filter(Drag::shouldRender)
			.forEach(widget -> {
				widget.updatePos(event.resolution);
				widget.render(event.resolution);
			});
    };
    
    @EventLink
    public final Listener<ChatGUIEvent> onChatGui = event -> {
		if (!isDisplay()) return;

		SList<Drag> widgets = Haru.dragMngr.getDragList();

		Optional<Drag> draggingOpt = widgets.stream()
			.filter(widget -> widget.shouldRender() && widget.dragging)
			.findFirst();

		Drag draggingWidget = draggingOpt.orElse(null);

		widgets.stream()
			.filter(Drag::shouldRender)
			.forEach(widget -> widget.onChatGUI(
				event.mouseX,
				event.mouseY,
				draggingWidget == null || draggingWidget == widget,
				event.scaledResolution
			));
    };
	
	private boolean isDisplay() {
		if (!Display.isActive() || !Display.isVisible() || !Display.isCreated()) return false;
		if (getModule(ClickGUI.class).pref.isEnabled("HideElementsInGui") && mc.currentScreen == Haru.dropGui) return false;
		return true;
	}
}
