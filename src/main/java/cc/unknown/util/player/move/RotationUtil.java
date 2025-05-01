package cc.unknown.util.player.move;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.structure.vectors.Vector2d;
import cc.unknown.util.structure.vectors.Vector2f;
import cc.unknown.util.structure.vectors.Vector3d;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RotationUtil implements Accessor {
	public static float getAngleDifference(float a, float b) {
		return MathHelper.wrapAngleTo180_float(a - b);
	}

	public static boolean rayCastIgnoreWall(float yaw, float pitch, EntityLivingBase target) {
	    yaw = toPositive(yaw);

	    AxisAlignedBB box = target.getEntityBoundingBox();

	    List<Vector2d> angles = Stream.of(box.minX, box.maxX).flatMap(x -> Stream.of(box.minY, box.maxY).flatMap(y -> Stream.of(box.minZ, box.maxZ).map(z -> {
	    	Vec3 hitPos = new Vec3(x, y, z);
	    	float yawVal = toPositive(getYaw(hitPos));
	    	float pitchVal = getPitch(hitPos);
	    	return new Vector2d(yawVal, pitchVal);
	    }))).collect(Collectors.toList());

	    double minYaw = angles.stream().mapToDouble(v -> v.x).min().orElse(Double.MAX_VALUE);
	    double maxYaw = angles.stream().mapToDouble(v -> v.x).max().orElse(Double.MIN_VALUE);
	    double minPitch = angles.stream().mapToDouble(v -> v.y).min().orElse(Float.MAX_VALUE);
	    double maxPitch = angles.stream().mapToDouble(v -> v.y).max().orElse(Float.MIN_VALUE);

	    return yaw >= minYaw && yaw <= maxYaw && pitch >= minPitch && pitch <= maxPitch;
	}

	public static float toPositive(float yaw) {
		if (yaw > 0)
			return yaw;

		return 360 + (yaw % 360);
	}

	public static float getRotationDifference(final Vector2f a, final Vector2f b) {
		float yawDiff = Math.abs(getAngleDifference(a.x, b.x));
		float pitchDiff = Math.abs(a.y - b.y);
		return (float) Math.hypot(yawDiff, pitchDiff);
	}

	public static Vector2f getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
		double x = rotX - startX;
		double y = rotY - startY;
		double z = rotZ - startZ;
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
		float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
		return new Vector2f(yaw, pitch);
	}

	public static float calculateYawFromSrcToDst(final float yaw, final double srcX, final double srcZ, final double dstX, final double dstZ) {
		final double xDist = dstX - srcX;
		final double zDist = dstZ - srcZ;
		final float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0 / Math.PI) - 90.0F;
		return yaw + MathHelper.wrapAngleTo180_float(var1 - yaw);
	}

	public static Vec3 getBestHitVec(final Entity entity) {
		final Vec3 positionEyes = mc.thePlayer.getPositionEyes(1);
		final AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
		final double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
		final double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
		final double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
		return new Vec3(ex, ey, ez);
	}

	public static float getYaw(BlockPos pos) {
		return getYaw(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
	}

	public static float getYaw(AbstractClientPlayer from, Vec3 pos) {
		return from.rotationYaw + MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(pos.zCoord - from.posZ, pos.xCoord - from.posX)) - 90f - from.rotationYaw);
	}

	public static float getYaw(Vec3 pos) {
		return getYaw(mc.thePlayer, pos);
	}

	public static float getPitch(BlockPos pos) {
		return getPitch(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
	}

	public static MovingObjectPosition rayCast(final Vec3 from, final double distance, final float yaw, final float pitch) {
		final float n4 = -yaw * 0.017453292f;
		final float n5 = -pitch * 0.017453292f;
		final float cos = MathHelper.cos((float) (n4 - Math.PI));
		final float sin = MathHelper.sin((float) (n4 - Math.PI));
		final float n6 = -MathHelper.cos(n5);
		final Vec3 vec3 = new Vec3(sin * n6, MathHelper.sin(n5), cos * n6);
		return mc.theWorld.rayTraceBlocks(from, from.addVector(vec3.xCoord * distance, vec3.yCoord * distance, vec3.zCoord * distance), false, false, false);
	}

	public static MovingObjectPosition rayCast(final double distance, final float yaw, final float pitch) {
		final Vec3 getPositionEyes = mc.thePlayer.getPositionEyes(1.0f);
		return rayCast(getPositionEyes, distance, yaw, pitch);
	}

	public static float getPitch(AbstractClientPlayer from, Vec3 pos) {
		double diffX = pos.xCoord - from.posX;
		double diffY = pos.yCoord - (from.posY + from.getEyeHeight());
		double diffZ = pos.zCoord - from.posZ;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		return from.rotationPitch + MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - from.rotationPitch);
	}

	public static float getPitch(Vec3 pos) {
		return getPitch(mc.thePlayer, pos);
	}

	public static Vector2d faceTrajectory(Entity target, boolean predict, float predictSize, float gravity, float velocity) {
		EntityPlayerSP player = mc.thePlayer;

		double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0.0) - (player.posX + (predict ? player.posX - player.prevPosX : 0.0));
		double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0.0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? player.posY - player.prevPosY : 0.0)) - player.getEyeHeight();
		double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0.0) - (player.posZ + (predict ? player.posZ - player.prevPosZ : 0.0));
		double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

		velocity = Math.min((velocity * velocity + velocity * 2) / 3, 1f);

		float gravityModifier = 0.12f * gravity;
		return new Vector2d(Math.toDegrees(Math.atan2(posZ, posX)) - 90f, -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - gravityModifier * (gravityModifier * posSqrt * posSqrt + 2 * posY * velocity * velocity))) / (gravityModifier * posSqrt))));
	}

	public static Vector2d faceTrajectory(Entity target, boolean predict, float predictSize) {
		float gravity = 0.03f;
		float velocity = 0;
		return faceTrajectory(target, predict, predictSize, gravity, velocity);
	}

	public static Vec3 heuristics(Entity entity, Vec3 xyz) {
		double boxSize = 0.2;
		float f11 = entity.getCollisionBorderSize();
		double minX = MathHelper.clamp_double(xyz.xCoord - boxSize, entity.getEntityBoundingBox().minX - (double) f11, entity.getEntityBoundingBox().maxX + (double) f11);
		double minY = MathHelper.clamp_double(xyz.yCoord - boxSize, entity.getEntityBoundingBox().minY - (double) f11, entity.getEntityBoundingBox().maxY + (double) f11);
		double minZ = MathHelper.clamp_double(xyz.zCoord - boxSize, entity.getEntityBoundingBox().minZ - (double) f11, entity.getEntityBoundingBox().maxZ + (double) f11);
		double maxX = MathHelper.clamp_double(xyz.xCoord + boxSize, entity.getEntityBoundingBox().minX - (double) f11, entity.getEntityBoundingBox().maxX + (double) f11);
		double maxY = MathHelper.clamp_double(xyz.yCoord + boxSize, entity.getEntityBoundingBox().minY - (double) f11, entity.getEntityBoundingBox().maxY + (double) f11);
		double maxZ = MathHelper.clamp_double(xyz.zCoord + boxSize, entity.getEntityBoundingBox().minZ - (double) f11, entity.getEntityBoundingBox().maxZ + (double) f11);
		return new Vec3(MathHelper.clamp_double(xyz.xCoord + MathUtil.randomSin(), minX, maxX), MathHelper.clamp_double(xyz.yCoord + MathUtil.randomSin(), minY, maxY), MathHelper.clamp_double(xyz.zCoord + MathUtil.randomSin(), minZ, maxZ));
	}

	public Vec3 getNearestPointOnBox(AxisAlignedBB hitbox, Vec3 playerPos) {
		double nearestX = MathHelper.clamp_double(playerPos.xCoord, hitbox.minX, hitbox.maxX);
		double nearestY = MathHelper.clamp_double(playerPos.yCoord, hitbox.minY, hitbox.maxY);
		double nearestZ = MathHelper.clamp_double(playerPos.zCoord, hitbox.minZ, hitbox.maxZ);
		return new Vec3(nearestX, nearestY, nearestZ);
	}

	public static double nearestRotation(final AxisAlignedBB bb) {
	    final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);
	    List<Double> steps = IntStream.rangeClosed(0, 20).mapToDouble(i -> i * 0.05).boxed().collect(Collectors.toList());
	    return steps.stream().flatMap(x -> steps.stream().flatMap(y -> steps.stream().map(z -> new Vec3(bb.minX + (bb.maxX - bb.minX) * x, bb.minY + (bb.maxY - bb.minY) * y, bb.minZ + (bb.maxZ - bb.minZ) * z)))).min(Comparator.comparingDouble(vec -> eyes.squareDistanceTo(vec))).map(vec -> vec.distanceTo(eyes)).orElse(0D);
	}

	public static double getDistanceToEntityBoxFromPosition(double posX, double posY, double posZ, Entity entity) {
		Vec3 pos = getBestHitVec(entity);
		double xDist = Math.abs(pos.xCoord - posX);
		double yDist = Math.abs(pos.yCoord - posY + (double) mc.thePlayer.getEyeHeight());
		double zDist = Math.abs(pos.zCoord - posZ);
		return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D) + Math.pow(zDist, 2.0D));
	}

	public static double getDistanceToEntityBox(Entity entity) {
		Vec3 eyes = mc.thePlayer.getPositionEyes(1.0F);
		Vec3 pos = getBestHitVec(entity);
		double xDist = Math.abs(pos.xCoord - eyes.xCoord);
		double yDist = Math.abs(pos.yCoord - eyes.yCoord);
		double zDist = Math.abs(pos.zCoord - eyes.zCoord);
		return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D) + Math.pow(zDist, 2.0D));
	}

	public static Vector3d getCenterPointOnBB(final AxisAlignedBB hitBox, final double point) {
		final double xWidth = hitBox.maxX - hitBox.minX;
		final double zWidth = hitBox.maxZ - hitBox.minZ;
		final double height = hitBox.maxY - hitBox.minY;

		double centerX = hitBox.minX + xWidth / 2.0;
		double centerY = hitBox.minY + height * point;
		double centerZ = hitBox.minZ + zWidth / 2.0;
		
		return new Vector3d(centerX, centerY, centerZ);
	}

	public static Vector2f getAngles(Entity entity) {
		if (entity == null) return null;
		final EntityPlayerSP player = mc.thePlayer;
		final double diffX = entity.posX - player.posX, diffY = entity.posY + (entity.getEyeHeight() / 5 * 3) - (player.posY + player.getEyeHeight()), diffZ = entity.posZ - player.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F, pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new Vector2f(player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw), player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch));
	}

	public static void getLockRotation(Entity target, boolean vertical) {
		if (target != null) {
			Vector2f rotations = getAngles(target);
			if (rotations != null) {
				mc.thePlayer.rotationYaw = rotations.x;
				if (vertical) {
					mc.thePlayer.rotationPitch = rotations.y + 4.0F;
				}
			}

		}
	}
}