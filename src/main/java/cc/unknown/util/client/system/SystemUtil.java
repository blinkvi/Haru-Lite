package cc.unknown.util.client.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import cc.unknown.Haru;
import cc.unknown.util.Accessor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class SystemUtil implements Accessor {	
	private Sequence sequence;
	private Sequencer sequencer;
	
	@SneakyThrows
	public void playSound() {
	    new Thread(() -> {
	        try {
	            ResourceLocation resourceLocation = new ResourceLocation("haru/sound/elfen.mid");

	            InputStream midiStream = mc.getResourceManager().getResource(resourceLocation).getInputStream();

	            if (midiStream != null) {
	                sequencer = MidiSystem.getSequencer();
	                if (sequencer == null) {
	                    System.out.println("No se encontró un secuenciador MIDI.");
	                    return;
	                }

	                sequencer.open();

	                sequence = MidiSystem.getSequence(midiStream);
	                sequencer.setSequence(sequence);

	                sequencer.start();

	                while (sequencer.isRunning()) {
	                    Thread.sleep(1000);
	                }

	                sequencer.close();
	            } else {
	                System.out.println("No se pudo cargar el archivo MIDI.");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }).start();
	}
    
    public void stopSound() {
    	if (sequence != null) {
    		sequencer.close();
    	}
    }
    
    public static boolean checkFirstStart() {
        String content = "[Haru] Init Sound | DONT DELETE THIS";
        
        File soundDir = new File(Haru.MAIN_DIR, "sound");
        File soundFile = new File(soundDir, "sound.txt");
        
        if (!soundDir.exists()) {
            if (soundDir.mkdirs()) {
                System.out.println("Carpeta 'sound' creada.");
            } else {
                System.out.println("Error al crear la carpeta 'sound'.");
                return false;
            }
        }

        if (soundFile.exists()) {
            try {
                String fileContent = new String(Files.readAllBytes(soundFile.toPath()));
                
                if (!fileContent.equals(content)) {
                	Haru.firstStart = true;
                    Files.write(soundFile.toPath(), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                } else {
                	Haru.firstStart = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
        	Haru.firstStart = true;
            try {
                Files.write(soundFile.toPath(), content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        return Haru.firstStart;
    }
	
    public boolean isOptifineLoaded() {
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
