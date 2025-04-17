package cc.unknown.util.player.move;

import cc.unknown.event.player.MoveInputEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

public class MoveUtil implements Accessor {
    public static boolean isMoving() {
        return mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    public static double predictedSumMotion(final double motion, final int ticks) {
        if (ticks == 0) return motion;
        double sum = 0.0;
        double predicted = motion;
        sum += predicted;
        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
            sum += predicted;
        }

        return sum;
    }

    public static double getDistance(final double x, final double z) {
        final double xSpeed = mc.thePlayer.posX - x;
        final double zSpeed = mc.thePlayer.posZ - z;
        return MathHelper.sqrt_double(xSpeed * xSpeed + zSpeed * zSpeed);
    }
    
    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
    
    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }
    
    public static void keybindStop() {
    	KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    	KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
    	KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
    	KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
    }
    
    public static void fixMovement(final MoveInputEvent event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = MathUtil.wrappedDifference(angle, predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

}
