package cc.unknown.util.player.move;

import static java.lang.Math.hypot;

import org.jetbrains.annotations.NotNull;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.structure.vectors.Vector2f;
import cc.unknown.util.structure.vectors.Vector3d;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil implements Accessor {
	public static float getAngleDifference(float a, float b) {
		return MathHelper.wrapAngleTo180_float(a - b);
	}

	public static Vector2f getAngles(Entity entity) {
		if (entity == null)
			return null;
		final EntityPlayerSP player = mc.thePlayer;

		final double diffX = entity.posX - player.posX,
				diffY = entity.posY + (entity.getEyeHeight() / 5 * 3) - (player.posY + player.getEyeHeight()),
				diffZ = entity.posZ - player.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F,
				pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

		return new Vector2f(player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw),
				player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch));
	}

	public static float i(final double n, final double n2) {
		return (float) (Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
	}

	public static double distanceFromYaw(final Entity entity) {
		return Math.abs(MathHelper.wrapAngleTo180_double(i(entity.posX, entity.posZ) - mc.thePlayer.rotationYaw));
	}

	public static float getRotationDifference(final Entity entity) {
		Vector2f target = getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		return (float) hypot(Math.abs(getAngleDifference(target.x, mc.thePlayer.rotationYaw)),
				Math.abs(target.y - mc.thePlayer.rotationPitch));
	}

	public static float getRotationDifference(final Entity entity, final Entity entity2) {
		Vector2f target = getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vector2f target2 = getRotations(entity2.posX, entity2.posY + entity2.getEyeHeight(), entity2.posZ);
		return (float) hypot(Math.abs(getAngleDifference(target.x, target2.x)), Math.abs(target.y - target2.y));
	}

	public static float getRotationDifference(final Vector2f a, final Vector2f b) {
		float yawDiff = Math.abs(getAngleDifference(a.x, b.x));
		float pitchDiff = Math.abs(a.y - b.y);
		return (float) Math.hypot(yawDiff, pitchDiff);
	}

	public static Vector2f getRotations(double rotX, double rotY, double rotZ, double startX, double startY,
			double startZ) {
		double x = rotX - startX;
		double y = rotY - startY;
		double z = rotZ - startZ;
		double dist = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
		float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
		return new Vector2f(yaw, pitch);
	}

	public static Vector2f getRotations(double posX, double posY, double posZ) {
		return getRotations(posX, posY, posZ, mc.thePlayer.posX,
				mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
	}

	public static Vector2f getRotations(Vec3 vec) {
		return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public static Vector2f getRotations(BlockPos blockPos) {
		return getRotations(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, mc.thePlayer.posX,
				mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
	}

	public static float calculateYawFromSrcToDst(final float yaw, final double srcX, final double srcZ,
			final double dstX, final double dstZ) {
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

	public static float getYaw(@NotNull BlockPos pos) {
		return getYaw(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
	}

	public static float getYaw(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
		return from.rotationYaw + MathHelper
				.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(pos.zCoord - from.posZ, pos.xCoord - from.posX))
						- 90f - from.rotationYaw);
	}

	public static float getYaw(@NotNull Vec3 pos) {
		return getYaw(mc.thePlayer, pos);
	}

	public static float getPitch(@NotNull BlockPos pos) {
		return getPitch(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
	}

	public static float getPitch(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
		double diffX = pos.xCoord - from.posX;
		double diffY = pos.yCoord - (from.posY + from.getEyeHeight());
		double diffZ = pos.zCoord - from.posZ;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		return from.rotationPitch + MathHelper
				.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - from.rotationPitch);
	}

	public static float getPitch(@NotNull Vec3 pos) {
		return getPitch(mc.thePlayer, pos);
	}

	public static float[] faceTrajectory(Entity target, boolean predict, float predictSize, float gravity,
			float velocity) {
		EntityPlayerSP player = mc.thePlayer;

		double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0.0)
				- (player.posX + (predict ? player.posX - player.prevPosX : 0.0));
		double posY = target.getEntityBoundingBox().minY
				+ (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0.0)
				+ target.getEyeHeight() - 0.15
				- (player.getEntityBoundingBox().minY + (predict ? player.posY - player.prevPosY : 0.0))
				- player.getEyeHeight();
		double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0.0)
				- (player.posZ + (predict ? player.posZ - player.prevPosZ : 0.0));
		double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

		velocity = Math.min((velocity * velocity + velocity * 2) / 3, 1f);

		float gravityModifier = 0.12f * gravity;

		return new float[] { (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90f, (float) -Math
				.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity
						- gravityModifier * (gravityModifier * posSqrt * posSqrt + 2 * posY * velocity * velocity)))
						/ (gravityModifier * posSqrt))) };
	}

	public static float[] faceTrajectory(Entity target, boolean predict, float predictSize) {

		float gravity = 0.03f;
		float velocity = 0;

		return faceTrajectory(target, predict, predictSize, gravity, velocity);
	}

	public static Vec3 heuristics(Entity entity, Vec3 xyz) {
		double boxSize = 0.2;
		float f11 = entity.getCollisionBorderSize();
		double minX = MathHelper.clamp_double(xyz.xCoord - boxSize, entity.getEntityBoundingBox().minX - (double) f11,
				entity.getEntityBoundingBox().maxX + (double) f11);
		double minY = MathHelper.clamp_double(xyz.yCoord - boxSize, entity.getEntityBoundingBox().minY - (double) f11,
				entity.getEntityBoundingBox().maxY + (double) f11);
		double minZ = MathHelper.clamp_double(xyz.zCoord - boxSize, entity.getEntityBoundingBox().minZ - (double) f11,
				entity.getEntityBoundingBox().maxZ + (double) f11);
		double maxX = MathHelper.clamp_double(xyz.xCoord + boxSize, entity.getEntityBoundingBox().minX - (double) f11,
				entity.getEntityBoundingBox().maxX + (double) f11);
		double maxY = MathHelper.clamp_double(xyz.yCoord + boxSize, entity.getEntityBoundingBox().minY - (double) f11,
				entity.getEntityBoundingBox().maxY + (double) f11);
		double maxZ = MathHelper.clamp_double(xyz.zCoord + boxSize, entity.getEntityBoundingBox().minZ - (double) f11,
				entity.getEntityBoundingBox().maxZ + (double) f11);
		return new Vec3(MathHelper.clamp_double(xyz.xCoord + MathUtil.randomSin(), minX, maxX),
				MathHelper.clamp_double(xyz.yCoord + MathUtil.randomSin(), minY, maxY),
				MathHelper.clamp_double(xyz.zCoord + MathUtil.randomSin(), minZ, maxZ));
	}

	public static Vector2f calculate(final Vector3d from, final Vector3d to) {
		final Vector3d diff = to.subtract(from);
		final double distance = Math.hypot(diff.getX(), diff.getZ());
		final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (float) (180.0F / Math.PI)) - 90.0F;
		final float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * (float) (180.0F / Math.PI)));
		return new Vector2f(yaw, pitch);
	}

	public Vec3 getNearestPointOnBox(AxisAlignedBB hitbox, Vec3 playerPos) {
		double nearestX = MathHelper.clamp_double(playerPos.xCoord, hitbox.minX, hitbox.maxX);
		double nearestY = MathHelper.clamp_double(playerPos.yCoord, hitbox.minY, hitbox.maxY);
		double nearestZ = MathHelper.clamp_double(playerPos.zCoord, hitbox.minZ, hitbox.maxZ);

		return new Vec3(nearestX, nearestY, nearestZ);
	}

	public static float calculate(final double n, final double n2) {
		return (float) (Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
	}

	public static void setPlayerRotation(Vector2f targetRotation) {
		targetRotation = applySensitivityPatch(new Vector2f(targetRotation),
				new Vector2f(mc.thePlayer.prevRotationYaw, mc.thePlayer.prevRotationPitch));
		mc.thePlayer.rotationYaw = targetRotation.x;
		mc.thePlayer.rotationPitch = targetRotation.y;
	}

	public static Vector2f applySensitivityPatch(final Vector2f rotation, final Vector2f previousRotation) {
		final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F
				+ 0.2F);
		final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
		final float yaw = previousRotation.x
				+ (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
		final float pitch = previousRotation.y
				+ (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
		return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
	}

	public static Vector2f resetRotation(final Vector2f rotation) {
		if (rotation == null) {
			return null;
		}

		final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.x);
		final float pitch = mc.thePlayer.rotationPitch;
		return new Vector2f(yaw, pitch);
	}

	public static double nearestRotation(final AxisAlignedBB bb) {
		final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

		Vec3 vecRotation3d = null;

		for (double xSearch = 0D; xSearch <= 1D; xSearch += 0.05D) {
			for (double ySearch = 0D; ySearch < 1D; ySearch += 0.05D) {
				for (double zSearch = 0D; zSearch <= 1D; zSearch += 0.05D) {
					final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
							bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
					final double vecDist = eyes.squareDistanceTo(vec3);

					if (vecRotation3d == null || eyes.squareDistanceTo(vecRotation3d) > vecDist) {
						vecRotation3d = vec3;
					}
				}
			}
		}
		return vecRotation3d.distanceTo(eyes);
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

	public static double[] getCenterPointOnBB(final AxisAlignedBB hitBox, final double point) {
		final double xWidth = hitBox.maxX - hitBox.minX;
		final double zWidth = hitBox.maxZ - hitBox.minZ;
		final double height = hitBox.maxY - hitBox.minY;

		double centerX = hitBox.minX + xWidth / 2.0;
		double centerY = hitBox.minY + height * point;
		double centerZ = hitBox.minZ + zWidth / 2.0;

		return new double[] { centerX, centerY, centerZ };
	}

	public float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
		return getRotations(blockPos, enumFacing, 0.25, 0.25);
	}

	public float[] getRotations(BlockPos blockPos, EnumFacing enumFacing, double xz, double y) {
		double d = blockPos.getX() + 0.5 - mc.thePlayer.posX + enumFacing.getFrontOffsetX() * xz;
		double d2 = blockPos.getZ() + 0.5 - mc.thePlayer.posZ + enumFacing.getFrontOffsetZ() * xz;
		double d3 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - blockPos.getY()
				- enumFacing.getFrontOffsetY() * y;
		double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
		float f = (float) (Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
		float f2 = (float) (Math.atan2(d3, d4) * 180.0 / Math.PI);
		return new float[] { MathHelper.wrapAngleTo180_float(f), f2 };
	}

	public static float updateRotation(float yaw) {
		return updateRotation(yaw, -180, 180);
	}

	public static float updateRotation(float curRot, float destination, float speed) {
		float f = MathHelper.wrapAngleTo180_float(destination - curRot);

		if (f > speed) {
			f = speed;
		}

		if (f < -speed) {
			f = -speed;
		}

		return curRot + f;
	}

	public static Vector2f getEntityRotations(Entity target) {
		if (target == null) {
			return null;
		} else {
			double diffX = target.posX - mc.thePlayer.posX;
			double diffY;
			if (target instanceof EntityLivingBase) {
				EntityLivingBase x = (EntityLivingBase) target;
				diffY = x.posY + (double) x.getEyeHeight() * 0.9
						- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
			} else {
				diffY = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0
						- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
			}

			double diffZ = target.posZ - mc.thePlayer.posZ;
			float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
			float pitch = (float) (-(Math.atan2(diffY, MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)) * 180.0
					/ Math.PI));

			return new Vector2f(
					mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
					mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch));
		}
	}

}