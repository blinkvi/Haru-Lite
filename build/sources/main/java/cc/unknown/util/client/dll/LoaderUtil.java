package cc.unknown.util.client.dll;

import java.io.File;

import cc.unknown.Haru;

public class LoaderUtil {
    private static final String[] SLINKY_DLLS = {"slinky_library.dll", "slinkyhook.dll"};

    public static void loadSlinkyDLLs() {
        for (String dllName : SLINKY_DLLS) {
            injectDlls(new File(Haru.DLL_DIR, dllName));
        }
    }

    public static void loadVapeDLL() {
        injectDlls(new File(Haru.DLL_DIR, "vape.dll"));
    }

    private static void injectDlls(File dllFile) {
        try {
            if (!dllFile.exists()) {
                return;
            }

            System.load(dllFile.getAbsolutePath());
            System.out.println("DLL Cargada: " + dllFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}