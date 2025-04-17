package cc.unknown.util.alt;

import com.google.gson.JsonObject;

import cc.unknown.mixin.impl.IMinecraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

@AllArgsConstructor
@Getter
@Setter
public class Account {
    private String name;
    private String uuid;
    private String accessToken;

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
}
