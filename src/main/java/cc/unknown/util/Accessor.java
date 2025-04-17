package cc.unknown.util;

import com.google.gson.Gson;

import cc.unknown.Haru;
import cc.unknown.managers.ConfigManager;
import cc.unknown.managers.DragManager;
import cc.unknown.managers.ModuleManager;
import cc.unknown.module.Module;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.client.system.CustomLogger;
import cc.unknown.util.client.system.StopWatch;
import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public interface Accessor {
    static Minecraft mc = Minecraft.getMinecraft();
    
    default Haru getInstance() {
        return Haru.instance;
    }
    
    default boolean isInGame() {
        return mc.thePlayer != null && mc.theWorld != null;
    }
    
    default StopWatch getStopWatch() {
    	return new StopWatch();
    }
    
    default DropGui getDropGui() {
    	return getInstance().getDropGui();
    }
    
    default DragManager getDragManager() {
    	return getInstance().getDragManager();
    }
    
    default ModuleManager getModuleManager() {
    	return getInstance().getModuleManager();
    }
    
    default ConfigManager getCfgManager() {
    	return getInstance().getCfgManager();
    }
    
    default String getPrefix() {
    	return "[" + ColorUtil.pink + "H" + ColorUtil.white + "] ";
    }
    
    default String getPrefix(EnumChatFormatting color, String name, EnumChatFormatting color2) {
    	return "[" + color + name + color2 + "] ";
    }

    default CustomLogger getLogger() {
    	return Haru.instance.getLogger();
    }

    default <T extends Module> T getModule(final Class<T> clazz) {
        return getInstance().getModuleManager().getModule(clazz);
    }

    default Gson getGSON() {
        return getInstance().getGSON();
    }
}