package cc.unknown.module.impl.combat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.RotationUtil;
import cc.unknown.util.structure.vectors.Vec3;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {

	private final Mode mode = new Mode("Mode", this, "Regular", "Regular", "Lock");
	
	private final Slider hSpeed = new Slider("HorizontalSpeed", this, 3.4f, 0.1f, 20, 0.01f, () -> mode.is("Regular"));
	private final Slider hMult  = new Slider("HorizontalMult",  this, 3.5f, 0.1f, 20, 0.01f, () -> mode.is("Regular"));
	
	private Bool vertical = new Bool("Vertical", this, false);
	private Slider vSpeed = new Slider("VerticalSpeed", this, 2.1f, 0.1f, 20, 0.01f, () -> mode.is("Regular") && vertical.get());
	private Slider vMult = new Slider("VerticalMult", this, 2.3f, 0.1f, 20, 0.01f, () -> mode.is("Regular") && vertical.get());
	
	private final Slider angle = new Slider("Angle", this, 180, 0, 180, 1);
	private final Slider distance = new Slider("Distance", this, 4f, 1f, 8f, 0.1f);

	private final Mode speedMode = new Mode("Speed", this, () -> mode.is("Regular"), "Random", "Random", "Secure", "Gaussian");
	
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("MultiPoint", false),
			new Bool("RequireClicking", true),
			new Bool("LockTarget", false),
			new Bool("IgnoreFriends", false),
			new Bool("IgnoreInvisibles", false),
			new Bool("VisibilityCheck", true),
			new Bool("MouseOverEntity", false),
			new Bool("CheckBlockBreak", false),
			new Bool("WeaponsOnly", false)));
	
	private final Set<EntityPlayer> lockedTargets = new HashSet<>();
	private final Clock clock = new Clock();
	public EntityPlayer target;
	
	@Override
	public void onDisable() {
		target = null;
		lockedTargets.clear();
	}
	
	@SubscribeEvent
	public void onAttack(AttackEvent event) {        
		if (event.target instanceof EntityPlayer) {
            EntityPlayer newTarget = (EntityPlayer) event.target;            
			if (conditionals.isEnabled("LockTarget")) {
	            lockedTargets.add(newTarget);
	            if (target == null) {
	                target = newTarget;
	            }
			}
		}
	}
	
	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
	    if (event.phase == Phase.START || mc.currentScreen != null || !mc.inGameHasFocus) return;

	    if (conditionals.isEnabled("WeaponsOnly") && !InventoryUtil.isSword()) return;

	    if (conditionals.isEnabled("MouseOverEntity")) {
	        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
	            return;
	    }

	    if (conditionals.isEnabled("CheckBlockBreak") && mc.objectMouseOver != null) {
	        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
	        if (blockPos != null) {
	            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
	            if (block != Blocks.air && block != Blocks.lava && block != Blocks.water &&
	                block != Blocks.flowing_lava && block != Blocks.flowing_water) {
	                return;
	            }
	        }
	    }

	    if (mc.gameSettings.keyBindAttack.isKeyDown()) {
	        clock.reset();
	    }

	    if (conditionals.isEnabled("RequireClicking") && (clock.hasPassed(150) || !mc.thePlayer.isSwingInProgress)) {
	        return;
	    }

	    if (!conditionals.isEnabled("LockTarget") || target == null || !onTarget()) {
	        target = getEnemy();
	    }

	    if (target == null) return;
	    
	    switch (mode.getMode()) {
	    case "Lock":
	    	RotationUtil.getLockRotation(target, vertical.get());
	    	break;
	    case "Regular":
		    double yawOffset = MathUtil.randomDouble(Math.min(hSpeed.getValue(), hMult.getValue()) * 10f, Math.max(hSpeed.getValue(), hMult.getValue()) * 10f) / 180f;
		    double yawFov = (float) PlayerUtil.fovFromTarget(target);
		    double yawAdjustment = getSpeedRandomize(speedMode.getMode(), yawFov, yawOffset, hSpeed.getValue(), hMult.getValue());
	
		    double pitchOffset = MathUtil.randomDouble(Math.min(vSpeed.getValue(), vMult.getValue()) * 10f, Math.max(vSpeed.getValue(), vMult.getValue()) * 10f) / 90f;
		    double pitchEntity = (float) PlayerUtil.pitchFromTarget(target);
		    
		    double resultVertical = getSpeedRandomize(speedMode.getMode(), pitchEntity, pitchOffset, vSpeed.getValue(), vMult.getValue());
	
		    if (onTarget()) {
		        applyYaw(yawFov, yawAdjustment);
		        applyPitch(resultVertical);
		    } else {
		        applyYaw(yawFov, yawAdjustment);
		        applyPitch(resultVertical);
		    }
	    	break;
	    }
	}

	private EntityPlayer getEnemy() {
        int fov = (int) angle.getValue();
        Vec3 playerPos = new Vec3(mc.thePlayer);
        EntityPlayer bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (lockedTargets.contains(player) && !isValidTarget(player, fov)) {
                continue;
            }
            if (!isValidTarget(player, fov)) {
                continue;
            }
            
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
	
    private boolean onTarget() {
        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.entityHit == target;
    }
    
    private void applyYaw(double yawFov, double yawAdjustment) {
        if (isYawFov(yawFov)) {
            mc.thePlayer.rotationYaw += yawAdjustment;
        }
    }

	private void applyPitch(double resultVertical) {
	    if (vertical.get()) {
	    	double pitchAdjustment = resultVertical;
	    	double newPitch = mc.thePlayer.rotationPitch + pitchAdjustment;

	        mc.thePlayer.rotationPitch += pitchAdjustment;
	        mc.thePlayer.rotationPitch = (float) normalizePitch(newPitch);
	    }
	}

	private double getSpeedRandomize(String mode, double fov, double offset, double speed, double complement) {
		double randomComplement = 0, result = 0;

	    switch (mode) {
	        case "Random":
	            randomComplement = MathUtil.randomDouble(complement / 100f, (complement + 0.5f) / 100f);
	            result = calculateResult(fov, offset, speed, MathUtil.randomDouble(speed, speed + 0.3f));
	            break;

	        case "Secure":
	            randomComplement = MathUtil.nextSecureDouble(complement / 100f, (complement + 0.5f) / 180f);
	            result = calculateResult(fov, offset, speed, MathUtil.nextSecureDouble(speed, speed + 0.3f));
	            break;

	        case "Gaussian":
	        	double gaussianFactor = (float) MathUtil.randomGaussian(0.15f);
	            randomComplement = (complement + gaussianFactor) / 200f;
	            double gaussianSpeed = speed + (float) MathUtil.randomGaussian(0.15f);
	            gaussianSpeed = MathUtil.lerpDouble(speed, gaussianSpeed, 0.5f);
	            result = calculateResult(fov, offset, speed, gaussianSpeed);
	            break;
	    }

	    return randomComplement + result;
	}
	
	private double normalizePitch(double pitch) {
	    return pitch >= 90f ? pitch - 360f : pitch <= -90f ? pitch + 360f : pitch;
	}

	private double calculateResult(double fov, double offset, double speed, double randomizedSpeed) {
	    return -fov * offset + fov / (100f - randomizedSpeed);
	}
	
	private boolean isYawFov(double fov) {
		return fov > 1.0f || fov < -1.0f;
	}
}
