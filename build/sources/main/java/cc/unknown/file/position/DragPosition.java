package cc.unknown.file.position;

import java.io.File;

import com.google.gson.JsonObject;

import cc.unknown.Haru;
import cc.unknown.file.Directory;
import cc.unknown.ui.drag.Drag;

public class DragPosition extends Directory {
	public DragPosition(String name) {
		super(name, new File(Haru.MAIN_DIR + "/draggable", name + ".json"));
	}

	@Override
	public void load(JsonObject object) {
	    for (Drag widget : getDragManager().getDragList()) {
	        if (widget == null || widget.name == null) continue;
	        
	        if (object.has(widget.name)) {
	            JsonObject obj = object.get(widget.name).isJsonObject() ? object.get(widget.name).getAsJsonObject() : null;
	            if (obj == null) continue;
	            
	            if (obj.has("x") && !obj.get("x").isJsonNull()) {
	                widget.x = obj.get("x").getAsFloat();
	            }
	            if (obj.has("y") && !obj.get("y").isJsonNull()) {
	                widget.y = obj.get("y").getAsFloat();
	            }
	        }
	    }
	}

	@Override
	public JsonObject save() {
		JsonObject object = new JsonObject();
		for (Drag widget : getDragManager().getDragList()) {
			JsonObject widgetObj = new JsonObject();
			widgetObj.addProperty("x", widget.x);
			widgetObj.addProperty("y", widget.y);
			object.add(widget.name, widgetObj);
		}
		return object;
	}
}