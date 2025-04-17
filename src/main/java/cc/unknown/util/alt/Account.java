package cc.unknown.util.alt;

import com.google.gson.JsonObject;

import cc.unknown.mixin.impl.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Account {
    private String name;
    private String uuid;
    private String accessToken;

    public Account(String name, String uuid, String accessToken) {
        this.name = name;
        this.uuid = uuid;
        this.accessToken = accessToken;
    }

    public boolean login() {
        ((IMinecraft) Minecraft.getMinecraft()).setSession(new Session(name, uuid, accessToken, "mojang"));
        return true;
    }

    public boolean isValid() {
        return name != null && uuid != null && accessToken != null && !name.isEmpty() && !uuid.isEmpty() && !accessToken.isEmpty();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("uuid", uuid);
        object.addProperty("accessToken", accessToken);
        return object;
    }

    public void parseJson(JsonObject object) {
        if (object.has("name")) {
            name = object.get("name").getAsString();
        }
        if (object.has("uuid")) {
            uuid = object.get("uuid").getAsString();
        }
        if (object.has("accessToken")) {
            accessToken = object.get("accessToken").getAsString();
        }
    }

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
