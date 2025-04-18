package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

@Mixin(NetworkDispatcher.class)
public abstract class MixinNetworkDispatcher extends SimpleChannelInboundHandler<Packet> implements ChannelOutboundHandler {

}
