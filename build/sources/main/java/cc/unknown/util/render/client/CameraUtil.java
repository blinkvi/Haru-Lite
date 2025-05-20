package cc.unknown.util.render.client;

import cc.unknown.util.Accessor;

public class CameraUtil implements Accessor {
    public static float cameraYaw;
    public static float cameraPitch;
    public static boolean freelooking;

    public static void overrideMouse(float f3, float f4) {
        cameraYaw += f3 * 0.15F;
        cameraPitch -= f4 * 0.15F;
        cameraPitch = Math.max(-90.0F, Math.min(90.0F, cameraPitch));
    }

    public static float getYaw() {
        return freelooking ? cameraYaw : mc.thePlayer.rotationYaw;
    }

    public static float getPitch() {
        return freelooking ? cameraPitch : mc.thePlayer.rotationPitch;
    }

    public static float getPrevYaw() {
        return freelooking ? cameraYaw : mc.thePlayer.prevRotationYaw;
    }

    public static float getPrevPitch() {
        return freelooking ? cameraPitch : mc.thePlayer.prevRotationPitch;
    }

    public static void enable() {
    	freelooking = true;
        cameraYaw = mc.thePlayer.rotationYaw;
        cameraPitch = mc.thePlayer.rotationPitch;
    }

    public static void disable() {
    	freelooking = false;
        cameraYaw = mc.thePlayer.rotationYaw;
        cameraPitch = mc.thePlayer.rotationPitch;
    }
}