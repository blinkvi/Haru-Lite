package cc.unknown.mixin.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;

public class FMLHandshakeFilter extends SimpleChannelInboundHandler<FMLHandshakeMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLHandshakeMessage msg) throws Exception {
        if (!(msg instanceof FMLHandshakeMessage.RegistryData)) {
            ctx.fireChannelRead(msg);
        }
    }
}