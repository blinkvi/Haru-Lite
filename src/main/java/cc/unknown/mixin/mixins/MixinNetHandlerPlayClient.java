package cc.unknown.mixin.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.PostVelocityEvent;
import cc.unknown.ui.click.DropGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.IChatComponent;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
	@Shadow
	private Minecraft gameController;

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	private void handleCloseWindow(final S2EPacketCloseWindow packetIn, final CallbackInfo ci) {
		if (gameController.currentScreen instanceof DropGui) {
			ci.cancel();
		}
	}
	
	@Inject(method = "handleEntityVelocity", at = @At("RETURN"))
	public void onPostHandleEntityVelocity(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
		if (gameController.thePlayer == null && gameController.theWorld == null) return;
		
	    if (packetIn.getEntityID() == this.gameController.thePlayer.getEntityId()) {
	        Haru.eventBus.handle(new PostVelocityEvent());
	    }
	}
	
	@Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
	private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) { }
}
