package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.event.player.MoveInputEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput {
    @Shadow
    @Final
    private GameSettings gameSettings;

    @Overwrite
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown())  this.moveForward += 1.0F;
        if (this.gameSettings.keyBindBack.isKeyDown())     this.moveForward -= 1.0F;
        if (this.gameSettings.keyBindLeft.isKeyDown())     this.moveStrafe  += 1.0F;
        if (this.gameSettings.keyBindRight.isKeyDown())    this.moveStrafe  -= 1.0F;

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        MoveInputEvent event = new MoveInputEvent(moveForward, moveStrafe, jump, sneak, 0.3D);
        MinecraftForge.EVENT_BUS.post(event);

        this.moveForward = event.forward;
        this.moveStrafe  = event.strafe;
        this.jump        = event.jump;
        this.sneak       = event.sneak;

        if (this.sneak) {
            double multiplier = event.sneakSlowDownMultiplier;
            this.moveStrafe  *= multiplier;
            this.moveForward *= multiplier;
        }
    }
}
