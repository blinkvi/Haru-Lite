package cc.unknown.handlers;

import java.util.UUID;

import cc.unknown.Haru;
import cc.unknown.module.impl.visual.NameProtect;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.network.ServerUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;

public class DiscordHandler implements Accessor {
	public boolean running = true;
	private long timeElapsed = 0;
	private final String joinSecret = UUID.randomUUID().toString();
	private final String spectateSecret = UUID.randomUUID().toString();

	public void start() {
		this.timeElapsed = System.currentTimeMillis();
		NameProtect protect = getModule(NameProtect.class);
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();

		DiscordRPC.discordInitialize("1362856733151854854", handlers, true);

		new Thread("Discord RPC Callback") {
			@Override
			public void run() {
				while (running) {
					if (mc.thePlayer != null) {
						if (mc.isSingleplayer()) {
							updateStatus("", "Practicing godlike movement.");
						} else if (ServerUtil.isConnectedToKnownServer(mc.getCurrentServerData().serverIP)) {
							updateStatus("User: " + (protect.isEnabled() ? protect.checkName() : Haru.getUser()),
						             "Cheating on " + ServerUtil.serverName);
						} else if (mc.currentScreen instanceof GuiDownloadTerrain) {
							updateStatus("Loading world...", "Hope you didn't just crash.");
						}
					} else {
						if (mc.currentScreen instanceof GuiSelectWorld) {
							updateStatus("Browsing worlds...", "");
						} else if (mc.currentScreen instanceof GuiMultiplayer) {
							updateStatus("Looking for a server...", "");
						} else if (mc.currentScreen instanceof GuiDownloadTerrain) {
							updateStatus("Loading world...", "Hopefully not stuck in limbo.");
						} else {
							updateStatus("In MainMenu...", "Touching grass...");
						}
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
					DiscordRPC.discordRunCallbacks();
				}
			}
		}.start();
	}

	public void stop() {
		running = false;
		DiscordRPC.discordShutdown();
	}

	public void updateStatus(String line1, String line2) {
		DiscordRichPresence.Builder rpc = new DiscordRichPresence.Builder(line2).setDetails(line1)
				.setBigImage("logo", "Haru " + Haru.VERSION).setParty("discord.gg/MuF4YRQFht", 1, 4)
				.setSecrets(joinSecret, spectateSecret).setStartTimestamps(timeElapsed);

		DiscordRPC.discordUpdatePresence(rpc.build());
	}
}
