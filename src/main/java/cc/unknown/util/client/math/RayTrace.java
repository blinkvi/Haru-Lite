package cc.unknown.util.client.math;

import cc.unknown.util.structure.vectors.Vector2f;
import lombok.experimental.UtilityClass;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@UtilityClass
public final class RayTrace {
    public boolean doRayTrace(BuildSpeed s, Vector2f rotation, Vec3 from, AxisAlignedBB box, double dist) {
        boolean intersection = false;
        intersection = isIntersection(from, rotation, intersection, box, dist, s);
        return intersection;
    }

    private boolean isIntersection(Vec3 location, Vector2f rotation, boolean intersection, AxisAlignedBB boundingBox, double dist, BuildSpeed s) {
        final float yaw = (float) rotation.getY();
        final float pitch = (float) rotation.getX();
        final MovingObjectPosition result = rayCast(yaw, pitch, boundingBox, location, dist, s);
        intersection |= result != null && result.hitVec != null;
        return intersection;
    }

    private MovingObjectPosition rayCast(final float yaw, final float pitch, final AxisAlignedBB bb, Vec3 locationIn, double dist, BuildSpeed s) {
        double lastX = locationIn.xCoord,
                lastY = locationIn.yCoord,
                lastZ = locationIn.zCoord;
        Vec3 vec3 = new Vec3(lastX, lastY, lastZ),
                vec31 = getVectorForRotation(pitch, yaw, s),
                vec32 = vec3.add(new Vec3(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist));
        return bb.calculateIntercept(vec3, vec32);
    }

    public Vec3 getVectorForRotation(final float pitch, final float yaw, BuildSpeed s) {
        float f = FastMath.cos(-yaw * 0.017453292F - (float) Math.PI),
                f1 = GeneralMath.sin(-yaw * 0.017453292F - (float) Math.PI, s),
                f2 = -GeneralMath.cos(-pitch * 0.017453292F, s),
                f3 = GeneralMath.sin(-pitch * 0.017453292F, s);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}