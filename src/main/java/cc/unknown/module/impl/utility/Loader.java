package cc.unknown.module.impl.utility;

import java.io.File;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.dll.LoaderUtil;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.value.impl.Bool;

@ModuleInfo(name = "Loader", description = "Inject sliky or vape (debug enabled)", category = Category.UTILITY)
public class Loader extends Module {
    
    private static final String[] REQUIRED_DLLS = {"slinky_library.dll", "slinkyhook.dll", "vape.dll"};

    private Bool vape = new Bool("Vape DLL", this, false);
    private Bool slinky = new Bool("Slinky DLL", this, false);

    @Override
    public void onEnable() {
        File dir = Haru.DLL_DIR;

        if (!checkDLLs(dir)) {
        	String url = "https://files.catbox.moe/krfdp2.zip";
        	String finished = prefix() + "Descarga Finalizada, vuelve a activar el modulo...";
        	String starting = prefix() + "Descargando DLLs...";
        	
        	new Thread(() -> NetworkUtil.downloadResources(url, dir, "krfdp2.zip", "resources", starting, finished)).start();
        }

        if (vape.get() && new File(dir, "vape.dll").exists()) {
        	ChatUtil.display(getPrefix(ColorUtil.red, "Vape", ColorUtil.white) + "Inyectando DLL...");
            LoaderUtil.loadVapeDLL();
        }

        if (slinky.get() && new File(dir, "slinky_library.dll").exists() && new File(dir, "slinkyhook.dll").exists()) {
        	ChatUtil.display(getPrefix(ColorUtil.gold, "Slinky", ColorUtil.white) + "Inyectando DLL...");
            LoaderUtil.loadSlinkyDLLs();
        }

        toggle();
        super.onEnable();
    }

    private boolean checkDLLs(File directory) {
        if (!directory.exists()) {
            return false;
        }
        for (String dll : REQUIRED_DLLS) {
            if (!new File(directory, dll).exists()) {
                return false;
            }
        }
        return true;
    }
}