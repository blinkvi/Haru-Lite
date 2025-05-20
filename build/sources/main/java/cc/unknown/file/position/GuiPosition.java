package cc.unknown.file.position;

import java.io.File;

import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.file.Directory;
import cc.unknown.ui.click.PanelRenderer;

public class GuiPosition extends Directory {

	public GuiPosition(String name) {
		super(name, new File(Haru.MAIN_DIR + "/draggable", name + ".json"));
	}

	@Override
	public void load(JsonObject object) {
		for (PanelRenderer window : getDropGui().getWindows()) {
			if (window == null) continue;
			String name = window.getCategory().name();
			if (object.has(name)) {
				JsonObject data = object.getAsJsonObject(name);
				float x = data.has("x") ? data.get("x").getAsFloat() : 0;
				float y = data.has("y") ? data.get("y").getAsFloat() : 0;
				boolean expanded = data.has("open") && data.get("open").getAsBoolean();

				window.setPosition(x, y);
				window.setExpand(expanded);
			}
		}
	}

	@Override
	public JsonObject save() {
		JsonObject object = new JsonObject();
		for (PanelRenderer window : getDropGui().getWindows()) {
			JsonObject data = new JsonObject();
			data.addProperty("x", window.getX());
			data.addProperty("y", window.getY());
			data.addProperty("open", window.isExpand());
			object.add(window.getCategory().name(), data);
		}
		return object;
	}
}
