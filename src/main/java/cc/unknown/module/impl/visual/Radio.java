package cc.unknown.module.impl.visual;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.music.RadioPlayer;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.structure.list.SList;
import cc.unknown.value.impl.ModeValue;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "MusicPlayer", description = "Time to listen to radio.", category = Category.VISUAL)
public class Radio extends Module {
	
	private final ModeValue mode = new ModeValue("Mode", this, "Local", "Nightcore", "Phonk", "90s", "Depression", "Anime", "Rock", "Hct", "Wifer", "Local");
		
	private boolean started = false;
	   
	private SList<File> musicFiles;
	private volatile AdvancedPlayer player;
	private final RadioPlayer radio = new RadioPlayer();
	
    @Override
    public void onEnable() {
    	started = true;
    }

    @Override
    public void onDisable() {   
        stopMusic();
        stopLocal();
        started = false;
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
	public void onPreTick(ClientTickEvent event) {
    	if (event.phase == Phase.END) return;
        if (started) {
        	if (mode.is("Local")) {
        	    String userHome = System.getProperty("user.home");
        	    String oneDrivePath = userHome + File.separator + "OneDrive" + File.separator + "Music";
        	    String defaultMusicPath = userHome + File.separator + "Music";

        	    File oneDriveMusic = new File(oneDrivePath);
        	    String musicFolderPath = oneDriveMusic.exists() ? oneDrivePath : defaultMusicPath;

        	    loadMusicFiles(musicFolderPath);
                if (musicFiles != null && !musicFiles.isEmpty()) {
                    playSong();
                } else {
                	ChatUtil.display(prefix("No local music files found."));
                }
            } else {
            	String songName = mode.getMode();
            	songName = songName.toLowerCase();
                ChatUtil.display(prefix("Playing " + songName + " playlist."));
                playOtherMusic(mode.getMode());
            }
            started = false;
        }
    };
    
    private void playLocal(File musicFile) {
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

    private void playOtherMusic(String mode) {
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
    		case "Wifer":
    			connectToMusic("reggaetonhits");
    			break;
            }
        };

        new Thread(musicRunnable).start();
    }
    
    private void connectToMusic(String instruction) {
        radio.start("https://stream.laut.fm/" + instruction);
    }

    private void loadMusicFiles(String directoryPath) {
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

    private void playSong() {
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
    
    private void stopMusic() {
    	Runnable musicRunnable = () -> {
            if (radio != null) {
                radio.stop();
            }
        };

        new Thread(musicRunnable).start();
    }
    
    private void stopLocal() {
        if (player != null) {
            player.close();
            player = null;
        }
    }
}