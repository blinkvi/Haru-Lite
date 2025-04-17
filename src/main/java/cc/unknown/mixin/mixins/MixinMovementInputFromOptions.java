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

		if (this.gameSettings.keyBindForward.isKeyDown()) {
			this.moveForward++;
		}

		if (this.gameSettings.keyBindBack.isKeyDown()) {
			this.moveForward--;
		}

		if (this.gameSettings.keyBindLeft.isKeyDown()) {
			this.moveStrafe++;
		}

		if (this.gameSettings.keyBindRight.isKeyDown()) {
			this.moveStrafe--;
		}

		this.jump = this.gameSettings.keyBindJump.isKeyDown();
		this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

		final MoveInputEvent event = new MoveInputEvent(moveForward, moveStrafe, jump, sneak, 0.3D);

		MinecraftForge.EVENT_BUS.post(event);

		final double sneakMultiplier = event.getSneakSlowDownMultiplier();
		this.moveForward = event.getForward();
		this.moveStrafe = event.getStrafe();
		this.jump = event.isJump();
		this.sneak = event.isSneak();

		if (this.sneak) {
			this.moveStrafe = (float) ((double) this.moveStrafe * sneakMultiplier);
			this.moveForward = (float) ((double) this.moveForward * sneakMultiplier);
		}
	}
}
