package cc.unknown.module.impl.combat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.lwjgl.input.Mouse;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.structure.vectors.Vec3;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {

	private final SliderValue horizontalSpeed = new SliderValue("HorizontalSpeed", this, 45, 5, 100);
	private final SliderValue horizontalCompl = new SliderValue("HorizontalMult", this, 35, 2, 97);
	
	private BoolValue vertical = new BoolValue("Vertical", this, false);
	private SliderValue verticalSpeed = new SliderValue("VerticalSpeed", this, 10, 1, 15, vertical::get);
	private SliderValue verticalCompl = new SliderValue("VerticalMult", this, 5, 1, 10, vertical::get);
	
	private final SliderValue angle = new SliderValue("Angle", this, 180, 1, 180, 1);
	private final SliderValue distance = new SliderValue("Distance", this, 4f, 1f, 8f, 0.1f);

	private final ModeValue speedMode = new ModeValue("Speed", this, "Random", "Random", "Secure", "Gaussian");
	
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("MultiPoint", false),
			new BoolValue("RequireClicking", true),
			new BoolValue("LockTarget", false),
			new BoolValue("IgnoreFriends", false),
			new BoolValue("IgnoreInvisibles", false),
			new BoolValue("VisibilityCheck", true),
			new BoolValue("MouseOverEntity", false),
			new BoolValue("CheckBlockBreak", false),
			new BoolValue("WeaponsOnly", false)));
	
	private final Set<EntityPlayer> lockedTargets = new HashSet<>();
	public EntityPlayer target;
	
	@SubscribeEvent
	public void onAttack(AttackEvent event) {        
		if (event.getTarget() instanceof EntityPlayer) {
            EntityPlayer newTarget = (EntityPlayer) event.getTarget();
			if (!isValidTarget(newTarget, (int) angle.getValue())) return;
			if (conditionals.isEnabled("LockTarget")) {
	            if (!lockedTargets.contains(newTarget)) lockedTargets.add(newTarget);
	            if (target == null) {
	                target = newTarget;
	            }
			}
		}
	}
	
	@Override
	public void onUpdate() {
	    if (noAim()) return;
	    
        if (!conditionals.isEnabled("LockTarget") || target == null || !onTarget()) {
            target = getEnemy();
        }
        
	    if (target == null) {
	        return;
	    }

	    float yawSpeed = horizontalSpeed.getValue();
	    float yawCompl = horizontalCompl.getValue();
	    float yawOffset = MathUtil.nextSecure(yawSpeed, yawCompl).floatValue() / 180f;
	    float yawFov = (float) PlayerUtil.fovFromTarget(target, mc.thePlayer.rotationYaw);
	    float pitchEntity = (float) PlayerUtil.pitchFromTarget(target, 0, mc.thePlayer.rotationPitch);
	    float yawAdjustment = getSpeedRandomize(speedMode.getMode(), yawFov, yawOffset, yawSpeed, yawCompl);

	    float verticalRandomOffset = MathUtil.nextRandom(verticalCompl.getValue() - 1.47328f, verticalCompl.getValue() + 2.48293f).floatValue() / 100;
	    float resultVertical = (float) (-(pitchEntity * verticalRandomOffset + pitchEntity / (101.0f - MathUtil.nextRandom(verticalSpeed.getValue() - 4.723847f, verticalSpeed.getValue()).floatValue())));
	    
	    if (onTarget(target)) {
	        applyYaw(yawFov, yawAdjustment);
	        applyPitch(resultVertical);
	    } else {
	        applyYaw(yawFov, yawAdjustment);
	        applyPitch(resultVertical);
	    }
	}

	@Override
	public void onDisable() {
		target = null;
	}

    private EntityPlayer getEnemy() {
        int fov = (int) angle.getValue();
        Vec3 playerPos = new Vec3(mc.thePlayer);
        EntityPlayer bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (lockedTargets.contains(player) && !isValidTarget(player, fov)) continue;
            if (lockedTargets.contains(player)) continue;
            if (!isValidTarget(player, fov)) continue;
            
            double score = playerPos.distanceTo(player.getPositionVector());
            if (score < bestScore) {
                bestTarget = player;
                bestScore = score;
            }
        }
        
        return bestTarget;
    }
	
	private boolean isValidTarget(EntityPlayer player, int fov) {
		if (player == mc.thePlayer || !player.isEntityAlive()) return false;
	    if (PlayerUtil.unusedNames(player)) return false;
	    if (conditionals.isEnabled("MultiPoint") && mc.pointedEntity != null && mc.pointedEntity == target) return false;
	    if (!conditionals.isEnabled("IgnoreInvisibles") && player.isInvisible()) return false;
	    if (FriendUtil.isFriend(player) && conditionals.isEnabled("IgnoreFriends")) return false;
	    if (PlayerUtil.isTeam(player) && isEnabled(Teams.class)) return false;
	    if (mc.thePlayer.getDistanceToEntity(player) > distance.getValue()) return false;
	    if (conditionals.isEnabled("VisibilityCheck") && !mc.thePlayer.canEntityBeSeen(player)) return false;
	    return fov == 180 || PlayerUtil.fov(fov, player);
	}

	private boolean noAim() {
	    if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
	    if (conditionals.isEnabled("WeaponsOnly") && !InventoryUtil.isSword()) return true;
	    if (conditionals.isEnabled("RequireClicking") && !Mouse.isButtonDown(0)) return true;
	    if (conditionals.isEnabled("MouseOverEntity") && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)) return true;

	    if (conditionals.isEnabled("CheckBlockBreak")) {
	    	BlockPos blockPos = mc.objectMouseOver.getBlockPos();
	    	if (blockPos != null) {
	    		Block block = mc.theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ())).getBlock();
	    		if (block != Blocks.air && block != Blocks.lava && block != Blocks.water && block != Blocks.flowing_lava && block != Blocks.flowing_water)
	    			return true;
	    	}
	    }

	    return false;
	}
	
    private boolean onTarget() {
        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
                && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                && mc.objectMouseOver.entityHit == target;
    }
    
    private boolean onTarget(EntityPlayer target) {
    	return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
    			&& mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
    			&& mc.objectMouseOver.entityHit == target;
    }
	
	private void applyYaw(double yawFov, float yawAdjustment) {
	    if (isYawFov(yawFov)) {
	        mc.thePlayer.rotationYaw += yawAdjustment;
	    }
	}

	private void applyPitch(float resultVertical) {
	    if (vertical.get()) {
	        float pitchAdjustment = resultVertical;
	        float newPitch = mc.thePlayer.rotationPitch + pitchAdjustment;

	        mc.thePlayer.rotationPitch += pitchAdjustment;
	        mc.thePlayer.rotationPitch = normalizePitch(newPitch);
	    }
	}

	private float normalizePitch(float pitch) {
	    return pitch >= 90f ? pitch - 360f : pitch <= -90f ? pitch + 360f : pitch;
	}

	private float getSpeedRandomize(String mode, double fov, double offset, double speed, double complement) {
	    double randomComplement;
	    float result;

	    switch (mode) {
	        case "Random":
	            randomComplement = MathUtil.nextRandom(complement - 1.47328, complement + 2.48293).doubleValue() / 100;
	            result = calculateResult(fov, offset, speed, MathUtil.nextRandom(speed - 4.723847, speed).doubleValue());
	            break;

	        case "Secure":
	            randomComplement = MathUtil.getSafeRandom((long) complement - (long)1.47328, (long) (complement + 2.48293)) / 100;
	            result = calculateResult(fov, offset, speed, MathUtil.getSafeRandom((long) ((long) speed - 4.723847), (long) speed));
	            break;

	        case "Gaussian":
	            randomComplement = (complement + new Random().nextGaussian() * 0.5) / 100;
	            result = calculateResult(fov, offset, speed, speed + new Random().nextGaussian() * 0.3);
	            break;

	        default:
	            throw new IllegalArgumentException("Unknown mode: " + mode);
	    }

	    return (float) (randomComplement + result);
	}

	private float calculateResult(double fov, double offset, double speed, double randomizedSpeed) {
	    return (float) (-(fov * offset + fov / (101.0 - randomizedSpeed)));
	}
	
	private boolean isYawFov(double fov) {
		return fov > 1.0D || fov < -1.0D;
	}
}
