package cc.unknown.module.impl.visual;

import java.io.File;

import javax.management.Notification;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.music.radio.RadioUtil;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.value.impl.Mode;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "MusicPlayer", description = "Time to listen to radio.", category = Category.VISUAL)
public class MusicPlayer extends Module {
	
	private final Mode station = new Mode("Station", this, "Nightcore", "Local", "Phonk", "90s", "Depression", "Anime", "Rock", "Hct", "Reggaeton", "Nightcore");
	public Notification musicNotification;
	private boolean started = false;
		
    @Override
    public void onEnable() {
    	started = true;
    }

    @Override
    public void onDisable() {   
        RadioUtil.stopMusic();
        RadioUtil.stopLocal();
        started = false;
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
	public void onPreTick(ClientTickEvent event) {
    	if (event.phase == Phase.START && isInGame()) {
	        if (started) {
            	if (station.is("Local")) {
            	    String userHome = System.getProperty("user.home");
            	    String oneDrivePath = userHome + File.separator + "OneDrive" + File.separator + "Music";
            	    String defaultMusicPath = userHome + File.separator + "Music";

            	    File oneDriveMusic = new File(oneDrivePath);
            	    String musicFolderPath = oneDriveMusic.exists() ? oneDrivePath : defaultMusicPath;

            	    RadioUtil.loadMusicFiles(musicFolderPath);
                    if (RadioUtil.musicFiles != null && !RadioUtil.musicFiles.isEmpty()) {
                    	RadioUtil.playSong();
                    } else {
                    	ChatUtil.display(prefix("No local music files found."));
                    }
                } else {
                	String songName = station.getMode();
                	songName = songName.toLowerCase();
                    ChatUtil.display(prefix("Playing " + songName + " playlist."));
                    RadioUtil.playOtherMusic(station.getMode());
                }
	            started = false;
	        }
    	}
    };
}