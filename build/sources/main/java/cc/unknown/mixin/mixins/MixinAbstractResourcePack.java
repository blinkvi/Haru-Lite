package cc.unknown.mixin.mixins;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cc.unknown.util.render.client.AutoScaler;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.AbstractResourcePack;

@Mixin(AbstractResourcePack.class)
public abstract class MixinAbstractResourcePack {
	
	@Shadow
	protected abstract InputStream getInputStreamByName(String name) throws IOException;
	
	@Inject(method = "getPackImage", at = @At("HEAD"), cancellable = true)
	private void patcher$downscalePackImage(CallbackInfoReturnable<BufferedImage> cir) throws IOException {
	    BufferedImage image = TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
	    if (image == null) {
	        cir.setReturnValue(null);
	        return;
	    }

	    BufferedImage buffer = AutoScaler.scaleImage(image);
	    cir.setReturnValue(buffer);
	}
}
