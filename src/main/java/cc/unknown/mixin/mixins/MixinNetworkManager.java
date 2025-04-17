package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.event.player.InboundEvent;
import cc.unknown.event.player.OutgoingEvent;
import cc.unknown.util.client.netty.PPSCounter;
import cc.unknown.util.client.netty.PacketUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("all")
@Mixin(value = NetworkManager.class, priority = 1001)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet<?>> {
	
	@Shadow private INetHandler packetListener;
	
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet != null) {
            if (PacketUtil.skipSendEvent.contains(packet)) {
                PacketUtil.skipSendEvent.remove(packet);
                return;
            }
        }
        
        OutgoingEvent event = new OutgoingEvent(packet);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
                
        PPSCounter.registerType(PPSCounter.PacketType.SEND);
    }
    
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo ci) {
        if (packet != null) {
            if (PacketUtil.skipReceiveEvent.contains(packet)) {
            	PacketUtil.skipReceiveEvent.remove(packet);
                return;
            }
        }
        
        InboundEvent event = new InboundEvent(packet);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
                
        PPSCounter.registerType(PPSCounter.PacketType.RECEIVED);
    }

    @Redirect(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V"))
    public void onProcessPacket(Packet instance, INetHandler handler) {
        try {
            instance.processPacket(this.packetListener);
        } catch (ThreadQuickExitException e) {
            throw e;
        } catch (Exception e) {
            //ExploitFixer.onBadPacket(instance, e);
        }
    }
}
