package cc.unknown.util.client.music.radio;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import cc.unknown.module.impl.visual.MusicPlayer;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.system.Clock;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class RadioPlayer implements Accessor {
    private Thread thread;
    private Player player = null;
    private final Clock timer = new Clock();

    public void start(final String url) {
    	MusicPlayer musicPlayer = getModule(MusicPlayer.class);
    	assert musicPlayer != null;
    	
        if (this.timer.hasPassed(5L)) {
            (this.thread = new Thread(() -> {
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                    try {
                        this.player = new Player(new URL(url).openStream());
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }

                    //setVolume();
                    this.player.play();
                } catch (JavaLayerException | NoSuchAlgorithmException | KeyManagementException e2) {
                	e2.printStackTrace();
                }
            })).start();
            this.timer.reset();
        }
    }

    public void stop() {
    	Runnable musicTask = () -> {
	        if (this.thread != null) {
	            this.thread.interrupt();
	            this.thread = null;
	        }
	        if (this.player != null) {
	            this.player.close();
	            this.player = null;
	        }
        };

        new Thread(musicTask).start();
    }
}