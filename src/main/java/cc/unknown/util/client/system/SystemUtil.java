package cc.unknown.util.client.system;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.sun.jna.Platform;

import cc.unknown.Haru;
import cc.unknown.util.Accessor;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class SystemUtil implements Accessor {
	private static Player player;
	
	private static final String EXPECTED_CONTENT = "[Haru] Init Sound | DONT DELETE THIS";
	
    public static void playSound() {
        if (Platform.isWindows()) {
            ResourceLocation loc = new ResourceLocation("haru/sound/welcome.mp3");
            InputStream input = null;
			try {
				input = Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
            BufferedInputStream buffer = new BufferedInputStream(input);

            try {
				player = new Player(buffer);
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    
    public static void stopSound() {
    	if (Platform.isWindows()) {
	        if (player != null) {
	            player.close();
	        }
    	}
    }
    
	private static void initTempFile() {
	    final File dir = new File(Minecraft.getMinecraft().mcDataDir, Haru.NAME + File.separator + "sound");
	    final File firstInitFile = new File(dir, "sound.txt");
		
        if (firstInitFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(firstInitFile.toPath())).trim();
                if (!EXPECTED_CONTENT.equals(content)) {
                    if (firstInitFile.delete()) {
                        Haru.instance.getLogger().info("Delete sound.txt, invalid content.");
                        Haru.instance.firstStart = true;
                    } else {
                    	Haru.instance.getLogger().error("Failed.");
                        return;
                    }
                } else {
                    return;
                }
            } catch (IOException e) {
            	Haru.instance.getLogger().error("Failed to read", e);
                return;
            }
        } else {
        	Haru.instance.firstStart = true;
        }

        if (!dir.exists() && !dir.mkdirs()) {
        	Haru.instance.getLogger().error("Failed dir.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(firstInitFile))) {
            writer.write(EXPECTED_CONTENT);
            Haru.instance.getLogger().info("Created sound.txt.");
        } catch (IOException e) {
        	Haru.instance.getLogger().error("Failed created", e);
        }
	}

	public static boolean checkFirstStart() {
	    final File dir = new File(Minecraft.getMinecraft().mcDataDir, Haru.NAME + File.separator + "sound");
	    final File firstInitFile = new File(dir, "sound.txt");
		
        if (firstInitFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(firstInitFile.toPath())).trim();
                if (EXPECTED_CONTENT.equals(content)) {
                	Haru.instance.firstStart = false;
                    Haru.instance.getLogger().info("Start detected, canceling sound...");
                } else {
                	Haru.instance.firstStart = true;
                    Haru.instance.getLogger().info("Contenido incorrecto, activando sonido...");
                    initTempFile();
                }
            } catch (IOException e) {
            	Haru.instance.getLogger().error("Error leyendo el archivo sound.txt", e);
            }
        } else {
        	Haru.instance.firstStart = true;
            Haru.instance.getLogger().info("No sound.txt found, initializing sound...");
            initTempFile();
        }
        return false;
	}
	
    public static boolean isOptifineLoaded() {
        File modsFolder = new File(mc.mcDataDir, "run/mods");

        if (!modsFolder.exists() || !modsFolder.isDirectory()) {
            modsFolder = new File(mc.mcDataDir, "mods");
        }

        if (modsFolder.exists() && modsFolder.isDirectory()) {
            File[] modFiles = modsFolder.listFiles((dir, name) -> name.toLowerCase().contains("optifine") && name.endsWith(".jar"));

            if (modFiles != null && modFiles.length > 0) {
                return true;
            }
        }
        return false;
    }
}
