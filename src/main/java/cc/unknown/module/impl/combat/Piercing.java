package cc.unknown.module.impl.combat;

import java.util.List;

import cc.unknown.Haru;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.MouseEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "Piercing", description = "Allows you to hit through entities or blocks.", category = Category.COMBAT)
public class Piercing extends Module {
	
	@EventLink(value = Priority.VERY_HIGH)
	public final Listener<MouseEvent> onMouse = event -> {
		if (!PlayerUtil.isInGame())
			return;

		if (event.button >= 0 && event.buttonstate && PlayerUtil.isInGame()) {
			call();
		}
	};

	public void call() {
		if (!Haru.modMngr.getModule(Piercing.class).isEnabled())
			return;

		Object[] o = getEntity(3.0D, 0.0D, null);

		if (o != null) {
			if (!RotationUtil.rayCastIgnoreWall(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch,
					(EntityLivingBase) o[0]))
				return;
			if (!Haru.modMngr.getModule(Piercing.class).isEnabled()
					&& RotationUtil.rayCast(3.0D, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch) != null)
				return;

			Entity en = (Entity) o[0];
			mc.objectMouseOver = new MovingObjectPosition(en, (Vec3) o[1]);
			mc.pointedEntity = en;
		}

	}

	public Object[] getEntity(double reach, double expand, float[] rotations) {
		Entity zz2 = mc.getRenderViewEntity();
		Entity entity = null;
		if (zz2 == null) {
			return null;
		} else {
			mc.mcProfiler.startSection("pick");
			Vec3 zz3 = zz2.getPositionEyes(1.0F);
			Vec3 zz4;
			if (rotations != null) {
				zz4 = ReflectUtil.getVectorForRotation(rotations[1], rotations[0]);
			} else {
				zz4 = zz2.getLook(1.0F);
			}
			Vec3 zz5 = zz3.addVector(zz4.xCoord * reach, zz4.yCoord * reach, zz4.zCoord * reach);
			Vec3 hitVec = null;
			List<?> zz8 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(zz2, zz2.getEntityBoundingBox()
					.addCoord(zz4.xCoord * reach, zz4.yCoord * reach, zz4.zCoord * reach).expand(1.0D, 1.0D, 1.0D));
			double zz9 = reach;

			for (int zz10 = 0; zz10 < zz8.size(); ++zz10) {
				Entity zz11 = (Entity) zz8.get(zz10);
				if (zz11.canBeCollidedWith()) {
					float ex = (float) ((double) zz11.getCollisionBorderSize() * 1.0);
					AxisAlignedBB zz13 = zz11.getEntityBoundingBox().expand(ex, ex, ex);
					zz13 = zz13.expand(expand, expand, expand);
					MovingObjectPosition zz14 = zz13.calculateIntercept(zz3, zz5);
					if (zz13.isVecInside(zz3)) {
						if (0.0D < zz9 || zz9 == 0.0D) {
							entity = zz11;
							hitVec = zz14 == null ? zz3 : zz14.hitVec;
							zz9 = 0.0D;
						}
					} else if (zz14 != null) {
						double zz15 = zz3.distanceTo(zz14.hitVec);
						if (zz15 < zz9 || zz9 == 0.0D) {
							if (zz11 == zz2.ridingEntity) {
								if (zz9 == 0.0D) {
									entity = zz11;
									hitVec = zz14.hitVec;
								}
							} else {
								entity = zz11;
								hitVec = zz14.hitVec;
								zz9 = zz15;
							}
						}
					}
				}
			}

			if (zz9 < reach && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
				entity = null;
			}

			mc.mcProfiler.endSection();
			if (entity != null && hitVec != null) {
				return new Object[] { entity, hitVec };
			} else {
				return null;
			}
		}
	}

}
