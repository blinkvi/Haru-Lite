package cc.unknown.util.client.music.radio;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.structure.list.SList;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class RadioUtil {
	
	private volatile static AdvancedPlayer player;
	public static SList<File> musicFiles;
	private final static RadioPlayer radio = new RadioPlayer();
	
    public static void playLocal(File musicFile) {
    	Runnable musicRunnable = () -> {
    		try (FileInputStream fis = new FileInputStream(musicFile)) {
                player = new AdvancedPlayer(fis);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        playSong();
                    }
                });
  
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(musicRunnable).start();
    }

    public static void playOtherMusic(String mode) {
        Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
            
            switch (mode) {
    		case "Hct":
    			connectToMusic("hct");
    			break;
    		case "Phonk":
    			connectToMusic("phonk");
    			break;
    		case "Rock":
    			connectToMusic("ozzy-oscar");
    			break;
    		case "NCS":
    			connectToMusic("ncsradio");
    			break;
    		case "Nightcore":
    			connectToMusic("nightcoremusic");
    			break;
    		case "90s":
    			connectToMusic("eurobeat");
    			break;
    		case "Anime":
    			connectToMusic("anime-radio-switzerland");
    			break;
    		case "Depression":
    			connectToMusic("skyfm");
    			break;
    		case "Reggaeton":
    			connectToMusic("reggaetonhits");
    			break;
            }
        };

        new Thread(musicRunnable).start();
    }
    
    public static void connectToMusic(String instruction) {
        radio.start("https://stream.laut.fm/" + instruction);
    }

    public static void loadMusicFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            List<String> validExtensions = Arrays.asList(".mp3");
            musicFiles = new SList<>(Arrays.asList(directory.listFiles((dir, name) -> {
                for (String ext : validExtensions) {
                    if (name.endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            })));
        }
    }

    public static void playSong() {
        if (musicFiles != null && !musicFiles.isEmpty()) {
            File currentFile = musicFiles.fastRemoveFile(new Random().nextInt(musicFiles.size()));
            String fileName = currentFile.getName();
            String songName = fileName.replace(".mp3", "");
            
            songName = songName.substring(1).toLowerCase();

            ChatUtil.display(prefix("Playing " + songName));
            playLocal(currentFile);
        } else {
            ChatUtil.display(prefix("No more songs to play."));
        }
    }
    
    public static void stopMusic() {
    	Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
        };

        new Thread(musicRunnable).start();
    }
    
    public static void stopLocal() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
    
    private static String prefix(String msg) {
    	return "[" + ColorUtil.pink + "H" + ColorUtil.white + "] " + msg;
    }
    
}
