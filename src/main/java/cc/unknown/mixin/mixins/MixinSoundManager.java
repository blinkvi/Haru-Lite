package cc.unknown.mixin.mixins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager {

    @Shadow 
    public abstract boolean isSoundPlaying(ISound sound);

    @Shadow 
    @Final 
    private Map<String, ISound> playingSounds;

    @Shadow
    private boolean loaded;
    
    private final List<String> pausedSounds = new ArrayList<>();

    @Redirect(method = "pauseAllSounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;pause(Ljava/lang/String;)V", remap = false))
    private void onlyPauseSoundIfNecessary(@Coerce SoundSystem soundSystem, String sound) {
        if (isSoundPlaying(playingSounds.get(sound))) {
            soundSystem.pause(sound);
            pausedSounds.add(sound);
        }
    }

    @Redirect(method = "resumeAllSounds", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false))
    private Iterator<String> iterateOverPausedSounds(Set<String> keySet) {
        return pausedSounds.iterator();
    }
    
    @Inject(method = "resumeAllSounds", at = @At("TAIL"))
    private void clearPausedSounds(CallbackInfo ci) {
        pausedSounds.clear();
    }
}