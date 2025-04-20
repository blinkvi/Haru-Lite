package cc.unknown.mixin.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.renderer.texture.TextureUtil;

@Mixin(TextureUtil.class)
abstract class MixinTextureUtil {
	@ModifyConstant(method = "allocateTextureImpl(IIII)V", constant = @Constant(intValue = GL11.GL_RGBA), allow = 1, require = 1)
	private static int optimizeTextureAnimationUpdates(int GL_RGBA) {
		return GL11.GL_RGBA8;
	}
}