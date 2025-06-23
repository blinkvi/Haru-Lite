package cc.unknown.util.player.move;
import cc.unknown.util.Accessor;
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
    
    public static double direction() {
        float moveYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward != 0.0F && mc.thePlayer.moveStrafing == 0.0F) {
          moveYaw += (mc.thePlayer.moveForward > 0.0F) ? 0.0F : 180.0F;
        } else if (mc.thePlayer.moveForward != 0.0F && mc.thePlayer.moveStrafing != 0.0F) {
          if (mc.thePlayer.moveForward > 0.0F) {
            moveYaw += (mc.thePlayer.moveStrafing > 0.0F) ? -45.0F : 45.0F;
          } else {
            moveYaw -= (mc.thePlayer.moveStrafing > 0.0F) ? -45.0F : 45.0F;
          } 
          moveYaw += (mc.thePlayer.moveForward > 0.0F) ? 0.0F : 180.0F;
        } else if (mc.thePlayer.moveStrafing != 0.0F && mc.thePlayer.moveForward == 0.0F) {
          moveYaw += (mc.thePlayer.moveStrafing > 0.0F) ? -90.0F : 90.0F;
        } 
        return Math.floorMod((int)moveYaw, 360);
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
    
    public static float getStrafeYaw(float forward, float strafe) {
        float yaw = mc.thePlayer.rotationYaw;

        if((forward == 0) && (strafe == 0))
            return yaw;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafe > 0.0f)
            yaw -= strafingYaw;
        else if (strafe < 0.0f)
            yaw += strafingYaw;

        return yaw;
    }

}
