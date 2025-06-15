package cc.unknown.mixin.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.event.player.PostVelocityEvent;
import cc.unknown.event.player.PreVelocityEvent;
import cc.unknown.ui.click.DropGui;
import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient implements Accessor {
	@Shadow
	private Minecraft gameController;
    @Shadow
    private WorldClient clientWorldController;

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	private void handleCloseWindow(final S2EPacketCloseWindow packetIn, final CallbackInfo ci) {
		if (gameController.currentScreen instanceof DropGui) {
			ci.cancel();
		}
	}
	
    @Inject(method = "handleEntityVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"), cancellable = true)
    public void onPreHandleEntityVelocity(S12PacketEntityVelocity packet, CallbackInfo ci) {
        if (!isInGame()) return;

        if (packet.getEntityID() == mc.thePlayer.getEntityId()) {

            PreVelocityEvent event = new PreVelocityEvent(packet.getMotionX(), packet.getMotionY(), packet.getMotionZ());
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) ci.cancel();

            Entity entity = this.clientWorldController.getEntityByID(packet.getEntityID());
            entity.setVelocity((double) packet.getMotionX() / 8000.0, (double)packet.getMotionY() / 8000.0, (double)packet.getMotionZ() / 8000.0);
            ci.cancel();
        }
    }
	
	@Inject(method = "handleEntityVelocity", at = @At("RETURN"))
	public void onPostHandleEntityVelocity(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
		if (gameController.thePlayer == null && gameController.theWorld == null) return;
		
	    if (packetIn.getEntityID() == this.gameController.thePlayer.getEntityId()) {
	        MinecraftForge.EVENT_BUS.post(new PostVelocityEvent());
	    }
	}
	
	@Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
	private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) { }
}
