package cc.unknown.handlers;

import java.util.Optional;

import org.lwjgl.opengl.Display;

import cc.unknown.event.render.ChatGUIEvent;
import cc.unknown.event.render.Render2DEvent;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.ui.drag.Drag;
import cc.unknown.util.Accessor;
import cc.unknown.util.structure.list.SList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DragHandler implements Accessor {
	
	@SubscribeEvent
	public void onRender2D(Render2DEvent event) {
		if (!isDisplay()) return;

		getDragManager().getDragList().stream()
			.filter(Drag::shouldRender)
			.forEach(widget -> {
				widget.updatePos(event.resolution);
				widget.render(event.resolution);
			});
	}

	@SubscribeEvent
	public void onChatGui(ChatGUIEvent event) {
		if (!isDisplay()) return;

		SList<Drag> widgets = getDragManager().getDragList();

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
	}
	
	private boolean isDisplay() {
		if (!Display.isActive() || !Display.isVisible() || !Display.isCreated()) return false;
		if (getModule(ClickGUI.class).pref.isEnabled("HideElementsInGui") && mc.currentScreen == getDropGui()) return false;
		return true;
	}
}
