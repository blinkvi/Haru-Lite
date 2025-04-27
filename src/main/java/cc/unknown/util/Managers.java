package cc.unknown.util;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import net.minecraft.client.Minecraft;

public interface Managers {
	static Minecraft mc = Minecraft.getMinecraft();

    default <T extends Module> T getModule(final Class<T> clazz) {
        return Haru.modMngr.getModule(clazz);
    }
}
