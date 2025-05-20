package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.mixin.interfaces.FMLHandshakeFilter;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeCodec;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

@Mixin(value = NetworkDispatcher.class, remap = false)
public abstract class MixinNetworkDispatcher {

	@Final
	@Shadow
	private Side side;

	@Final
	@Shadow
	private EmbeddedChannel handshakeChannel;

	@Inject(method = "insertIntoChannel", at = @At("HEAD"))
	public void replayModRecording_setupForLocalRecording(CallbackInfo cb) {
		if (!handshakeChannel.attr(NetworkDispatcher.IS_LOCAL).get())
			return;

		if (side == Side.SERVER) {
			handshakeChannel.attr(NetworkDispatcher.IS_LOCAL).set(false);
		} else {
			ChannelPipeline pipeline = handshakeChannel.pipeline();
			pipeline.addAfter(pipeline.context(FMLHandshakeCodec.class).name(), "filter", new FMLHandshakeFilter());
		}
	}

	@Redirect(method = "clientListenForServerHandshake", at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/network/NetworkManager;setConnectionState(Lnet/minecraft/network/EnumConnectionState;)V"))
	public void raceConditionWorkAround1(NetworkManager self, EnumConnectionState ignored) { }

	@Redirect(method = "insertIntoChannel", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelConfig;setAutoRead(Z)Lio/netty/channel/ChannelConfig;"))
	public ChannelConfig raceConditionWorkAround2(ChannelConfig self, boolean autoRead) {
		if (side == Side.CLIENT) {
			autoRead = false;
		}
		return self.setAutoRead(autoRead);
	}
}
