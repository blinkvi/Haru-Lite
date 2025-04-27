package cc.unknown.mixin.mixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

	@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getPlayerEntityByUUID(Ljava/util/UUID;)Lnet/minecraft/entity/player/EntityPlayer;"))
	public EntityPlayer removePlayerHead(WorldClient instance, UUID uuid) {
		return null;
	}

	@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"))
	public boolean removePlayerHead(Minecraft instance) {
		return true;
	}

	@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;getIsencrypted()Z"))
	public boolean removePlayerHead(NetworkManager instance) {
		return true;
	}

	@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;getPlayerName(Lnet/minecraft/client/network/NetworkPlayerInfo;)Ljava/lang/String;"))
	private String renderTags(GuiPlayerTabOverlay instance, NetworkPlayerInfo info) {
	    String displayedName = instance.getPlayerName(info);
	    String realName = info.getGameProfile().getName();

	    boolean isOnCosmeticList = CosmeticSocket.cosmeticList.stream().anyMatch(cosmetic -> cosmetic.getName().equalsIgnoreCase(realName));

	    if (!isOnCosmeticList) {
	        return displayedName;
	    }

	    return ColorUtil.pink + " [" + ColorUtil.red + "User" + ColorUtil.pink + "] " + ColorUtil.reset + displayedName;
	}
}
