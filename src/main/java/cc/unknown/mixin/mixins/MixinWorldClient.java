package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.WorldLoadEvent;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

@Mixin(WorldClient.class)
public class MixinWorldClient {
    @ModifyConstant(method = "doVoidFogParticles", constant = @Constant(intValue = 1000))
    private int doVoidFogParticles(int original) {
        return 100;
    }
    
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectAtEndOfConstructor(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn, CallbackInfo ci) {
    	Haru.eventBus.handle(new WorldLoadEvent());
    }
    
}
