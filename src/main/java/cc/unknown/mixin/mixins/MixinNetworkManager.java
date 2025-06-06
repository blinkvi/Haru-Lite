package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.event.netty.InboundEvent;
import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.mixin.interfaces.INetworkManager;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.network.PPSCounter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("all")
@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet> implements INetworkManager {
	@Shadow
	private Channel channel;
	
	@Shadow
	private INetHandler packetListener;
	
	@Shadow
	public abstract boolean isChannelOpen();
	
	@Unique
	private Minecraft mc;

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet packet, CallbackInfo ci) {
        OutgoingEvent event = new OutgoingEvent(packet);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
                
        PPSCounter.registerType(PPSCounter.PacketType.SEND);
    }
    
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet packet, CallbackInfo ci) {
        InboundEvent event = new InboundEvent(packet);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
                
        PPSCounter.registerType(PPSCounter.PacketType.RECEIVED);
    }
    
    @Override
    public void addToSendQueueUnregistered(Packet packetIn) {
    	if (isChannelOpen()) {
    		ReflectUtil.flushOutboundQueue();
    		ReflectUtil.dispatchPacket(packetIn, (GenericFutureListener[])null);
    	} else {
    		ReflectUtil.readWriteLock().writeLock().lock();
    		try {
    			ReflectUtil.outboundPacketsQueue().add(ReflectUtil.InboundHandlerTuplePacketListener(packetIn));
    		} finally {
    			ReflectUtil.readWriteLock().writeLock().unlock();
    		} 
    	}
    }
    
    @Override
    public void addToReceiveQueueUnregistered(Packet packet) {
    	if (this.channel.isOpen())
    		try {
    			packet.processPacket(packetListener);
    		} catch (ThreadQuickExitException threadQuickExitException) {} 
    }
    
    @Override
    public void addToReceiveQueue(Packet packet) {
        if (this.channel.isOpen()) {
            try {
                final InboundEvent event = new InboundEvent(packet);
                MinecraftForge.EVENT_BUS.post(event);

                if (event.isCanceled()) {
                    return;
                }
                
                packet.processPacket(this.packetListener);
            } catch (final ThreadQuickExitException var4) {
            }
        }
    }
}
