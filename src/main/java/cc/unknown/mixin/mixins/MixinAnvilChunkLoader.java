package cc.unknown.mixin.mixins;

import java.io.DataInputStream;
import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader {

	@Redirect(method = "loadChunk__Async", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompressedStreamTools;read(Ljava/io/DataInputStream;)Lnet/minecraft/nbt/NBTTagCompound;"))
	private NBTTagCompound redirectReadChunkData(DataInputStream inputStream) throws IOException {
		try (DataInputStream stream = inputStream) {
			return CompressedStreamTools.read(stream);
		}
	}
}