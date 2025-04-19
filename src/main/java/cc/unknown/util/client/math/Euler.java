package cc.unknown.util.client.math;

import static java.lang.Math.PI;

import cc.unknown.util.structure.vectors.Vector2f;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.util.Vec3;

@UtilityClass
public final class Euler {

    public boolean compareTwoVectorAngle(@NonNull Vector2f a, @NonNull Vector2f b, double radi) {
        double angleA = Math.atan2(a.getY(), a.getX());
        double angleB = Math.atan2(b.getY(), b.getX());

        double angleDiff = Math.abs(angleA - angleB);

        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }

        return angleDiff <= Math.toRadians(radi);
    }

    public double calculateTwoVectorAngleDifference(@NonNull Vector2f a, @NonNull Vector2f b) {
        double angleA = Math.atan2(a.getY(), a.getX());
        double angleB = Math.atan2(b.getY(), b.getX());

        double angleDiff = Math.abs(angleA - angleB);

        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }
        return angleDiff;
    }

    public Vector2f calculateVec2Vec(final Vec3 from, final Vec3 to) {
        final Vec3 diff = to.subtract(from);
        final double distance = Math.hypot(diff.xCoord, diff.zCoord);
        final float yaw = (float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0F / PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(diff.yCoord, distance) * 180.0F / PI));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f calculateRotationToVec(final Vec3 pos) {
        final float deltaX = (float) pos.xCoord;
        final float deltaY = (float) pos.yCoord;
        final float deltaZ = (float) pos.zCoord;
        final float distance = (float) Math.hypot(deltaX, deltaZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);
        float pitch = (float) (Math.toDegrees(-Math.atan2(deltaY, distance)));

        return new Vector2f(wrapDegrees(yaw), clamp(pitch, -90, 90));
    }

    public float wrapDegrees(float value) {
        float f = value % 360.0F;

        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    public double calculateVectorAngle(@NonNull Vector2f a) {
        return Math.atan2(a.getY(), a.getX());
    }
}