package cc.unknown.util;

import com.google.gson.Gson;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import net.minecraft.client.Minecraft;

public interface Accessor {
    static Minecraft mc = Minecraft.getMinecraft();

    default Gson getGSON() {
        return Haru.instance.GSON;
    }
    
    default <T extends Module> T getModule(final Class<T> clazz) {
        return Haru.modMngr.getModule(clazz);
    }
}