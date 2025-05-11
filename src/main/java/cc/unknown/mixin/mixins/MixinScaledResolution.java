package cc.unknown.mixin.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cc.unknown.util.render.client.ResolutionHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;

@Mixin(ScaledResolution.class)
public class MixinScaledResolution {
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;guiScale:I", opcode = Opcodes.GETFIELD))
    private int patcher$modifyScale(GameSettings gameSettings) {
        int scale = ResolutionHelper.getScaleOverride();
        return scale >= 0 ? scale : gameSettings.guiScale;
    }
}