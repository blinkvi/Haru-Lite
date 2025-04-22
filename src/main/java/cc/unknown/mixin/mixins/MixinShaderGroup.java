package cc.unknown.mixin.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import cc.unknown.mixin.interfaces.IShaderGroup;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;

@Mixin(ShaderGroup.class)
public abstract class MixinShaderGroup implements IShaderGroup {

	@Override
	@Accessor
	public abstract List<Shader> getListShaders();
}