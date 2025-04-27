package cc.unknown.mixin.mixins;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.ui.menu.AltManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen {
	
	@Shadow
	private ServerSelectionList serverListSelector;
	
	@Shadow
	private ServerData selectedServer;
	
	@Shadow
	private boolean addingServer;
	
	@Shadow
	private boolean deletingServer;

	@Shadow
    private boolean editingServer;
	
	@Shadow
	private boolean directConnect;
	
	@Shadow
	public abstract void connectToSelected();
	
	@Shadow
	public abstract void refreshServerList();
	
	@Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
	private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
		if (keyCode == Keyboard.KEY_LSHIFT) {
			mc.displayGuiScreen(new AltManager());
		}
	}

	@Overwrite
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.func_148193_k() < 0
					? null
					: this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());

			if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
				String s4 = ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData().serverName;

				if (s4 != null) {
					this.deletingServer = true;
					String s = I18n.format("selectServer.deleteQuestion", new Object[0]);
					String s1 = "\'" + s4 + "\' " + I18n.format("selectServer.deleteWarning", new Object[0]);
					String s2 = I18n.format("selectServer.deleteButton", new Object[0]);
					String s3 = I18n.format("gui.cancel", new Object[0]);
					GuiYesNo guiyesno = new GuiYesNo((GuiMultiplayer) (Object) this, s, s1, s2, s3, this.serverListSelector.func_148193_k());
					this.mc.displayGuiScreen(guiyesno);
				}
			} else if (button.id == 1) {
				this.connectToSelected();
			} else if (button.id == 4) {
				this.directConnect = true;
				this.mc.displayGuiScreen(new GuiScreenServerList((GuiMultiplayer) (Object) this,
						this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "",
								false)));
			} else if (button.id == 3) {
				this.addingServer = true;
				this.mc.displayGuiScreen(new GuiScreenAddServer((GuiMultiplayer) (Object) this,
						this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "",
								false)));
			} else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
				this.editingServer = true;
				ServerData serverdata = ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData();
				this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
				this.selectedServer.copyFrom(serverdata);
				this.mc.displayGuiScreen(new GuiScreenAddServer((GuiMultiplayer) (Object) this, this.selectedServer));
			} else if (button.id == 0) {
				this.mc.displayGuiScreen(new GuiMainMenu());
			} else if (button.id == 8) {
				this.refreshServerList();
			}
		}
	}
}
