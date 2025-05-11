package cc.unknown.util.render.client;

public class ResolutionHelper {
    private static int currentScaleOverride = -1;
    private static int scaleOverride = -1;

    public static int getCurrentScaleOverride() {
        return currentScaleOverride;
    }

    public static void setCurrentScaleOverride(int currentScaleOverride) {
        ResolutionHelper.currentScaleOverride = currentScaleOverride;
    }

    public static int getScaleOverride() {
        return scaleOverride;
    }

    public static void setScaleOverride(int scaleOverride) {
        ResolutionHelper.scaleOverride = scaleOverride;
    }

    public static int getInventoryScale() {
        return 2;
    }
}