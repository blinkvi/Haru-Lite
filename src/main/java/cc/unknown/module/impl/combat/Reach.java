package cc.unknown.module.impl.combat;

import java.util.Arrays;
import java.util.List;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "Reach", description = "Sets the attack range.", category = Category.COMBAT)
public class Reach extends Module {
	
    public final SliderValue min = new SliderValue("Min Range", this, 3.0F, 3, 6F, .1f);
    public final SliderValue max = new SliderValue("Max Range", this, 3.3F, 3, 6F, .1f);

    private final SliderValue chance = new SliderValue("Chance", this, 0.9f, 0f, 1f, 0.1f);
		
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("OnlyWeapon", false),
			new BoolValue("OnlyMove", false),
			new BoolValue("OnlySprint", false),
			new BoolValue("OnlySpeedPotion", false),
			new BoolValue("ComboMode", false),
			new BoolValue("TradeMode", false), 
			new BoolValue("TapMode", false), 
			new BoolValue("WaterCheck", true)));

	@Override
	public void onUpdate() {
		correctValues(min, max);
	}
	
	@Override
	public void guiUpdate() {
		correctValues(min, max);
	}
	
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
	    callReach();
	}
    
	private boolean callReach() {
		if (!isInGame()) {
			return false;
		} else if (conditionals.isEnabled("OnlyMove") && !MoveUtil.isMoving()) {
			return false;
		} else if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) {
			return false;
		} else if (conditionals.isEnabled("OnlySprint") && !mc.thePlayer.isSprinting()) {
			return false;
		} else if (conditionals.isEnabled("OnlySpeedPotion") && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			return false;
		} else if (conditionals.isEnabled("TradeMode") && (mc.thePlayer.hurtResistantTime > 0 || !mc.thePlayer.onGround)) {
			return false;
		} else if (conditionals.isEnabled("ComboMode") && (!(mc.thePlayer.hurtResistantTime > 0) && MoveUtil.isMoving())) {
			return false;
		} else if (conditionals.isEnabled("TapMode") && mc.thePlayer.moveForward == 0) {
			return false;
		} else if (conditionals.isEnabled("WaterCheck") && mc.thePlayer != null && mc.thePlayer.isInWater()) {
			return false;
		} else {
			if (!MathUtil.chanceApply(chance.getValue())) {
				return false;
			}

			double reach = MathUtil.randomizeDouble(min.getValue(), max.getValue());
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
		if (!this.isEnabled()) {
			reach = mc.playerController.extendedReach() ? 6.0D : 3.0D;
		}

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
