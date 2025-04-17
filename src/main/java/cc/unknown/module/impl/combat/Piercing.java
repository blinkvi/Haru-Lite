package cc.unknown.module.impl.combat;

import java.util.List;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "Piercing", description = "Allows you to hit through entities or blocks.", category = Category.COMBAT)
public class Piercing extends Module {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        applyPiercing();
	}
    
	private boolean applyPiercing() {
		if (!isInGame()) {
			return false;
		} else {
			if (mc.objectMouseOver != null) {
				BlockPos p = mc.objectMouseOver.getBlockPos();
				if (p != null && mc.theWorld.getBlockState(p).getBlock() != Blocks.air) {
					return false;
				}
			}

			double reach = 3.0D;
			Object[] object = findEntitiesWithinReach(reach);
			if (object == null) {
				return false;
			} else {
				Entity en = (Entity) object[0];
				mc.objectMouseOver = new MovingObjectPosition(en, (Vec3) object[1]);
				mc.pointedEntity = en;
				return true;
			}
		}
	}

	private Object[] findEntitiesWithinReach(double reach) {
		if (!this.isEnabled())
			reach = mc.playerController.extendedReach() ? 6.0D : 3.0D;
		
		Entity renderView = mc.getRenderViewEntity();
		Entity target = null;
		if (renderView == null) {
			return null;
		} else {
			mc.mcProfiler.startSection("pick");
			Vec3 eyePosition = renderView.getPositionEyes(1.0F);
			Vec3 playerLook = renderView.getLook(1.0F);
			Vec3 reachTarget = eyePosition.addVector(playerLook.xCoord * reach, playerLook.yCoord * reach,
					playerLook.zCoord * reach);
			Vec3 targetHitVec = null;
			List<Entity> targetsWithinReach = mc.theWorld.getEntitiesWithinAABBExcludingEntity(renderView,
					renderView.getEntityBoundingBox()
							.addCoord(playerLook.xCoord * reach, playerLook.yCoord * reach, playerLook.zCoord * reach)
							.expand(1.0D, 1.0D, 1.0D));
			double adjustedReach = reach;

			for (Entity entity : targetsWithinReach) {
				if (entity.canBeCollidedWith()) {
					float ex = (float) ((double) entity.getCollisionBorderSize());
					AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(ex, ex, ex);
					MovingObjectPosition targetPosition = entityBoundingBox.calculateIntercept(eyePosition,
							reachTarget);
					if (entityBoundingBox.isVecInside(eyePosition)) {
						if (0.0D < adjustedReach || adjustedReach == 0.0D) {
							target = entity;
							targetHitVec = targetPosition == null ? eyePosition : targetPosition.hitVec;
							adjustedReach = 0.0D;
						}
					} else if (targetPosition != null) {
						double distanceToVec = eyePosition.distanceTo(targetPosition.hitVec);
						if (distanceToVec < adjustedReach || adjustedReach == 0.0D) {
							if (entity == renderView.ridingEntity) {
								if (adjustedReach == 0.0D) {
									target = entity;
									targetHitVec = targetPosition.hitVec;
								}
							} else {
								target = entity;
								targetHitVec = targetPosition.hitVec;
								adjustedReach = distanceToVec;
							}
						}
					}
				}
			}

			if (adjustedReach < reach && !(target instanceof EntityLivingBase)
					&& !(target instanceof EntityItemFrame)) {
				target = null;
			}

			mc.mcProfiler.endSection();
			if (target != null && targetHitVec != null) {
				return new Object[] { target, targetHitVec };
			} else {
				return null;
			}
		}
	}
}
