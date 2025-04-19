package cc.unknown.module.impl.combat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Mouse;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
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
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {

	private final SliderValue hSpeed = new SliderValue("HorizontalSpeed", this, 3.4f, 0.1f, 20, 0.01f);
	private final SliderValue hMult  = new SliderValue("HorizontalMult",  this, 3.5f, 0.1f, 20, 0.01f);
	
	private BoolValue vertical = new BoolValue("Vertical", this, false);
	private SliderValue vSpeed = new SliderValue("VerticalSpeed", this, 2.1f, 0.1f, 20, 0.01f, vertical::get);
	private SliderValue vMult = new SliderValue("VerticalMult", this, 2.3f, 0.1f, 20, 0.01f, vertical::get);
	
	private final SliderValue angle = new SliderValue("Angle", this, 180, 0, 180, 1);
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
	    if (event.phase == Phase.START) return;
	    if (noAim()) return;

        if (!conditionals.isEnabled("LockTarget") || target == null || !onTarget()) {
            target = getEnemy();
        }
        
	    if (target == null) {
	        return;
	    }
	    
	    float yawOffset = MathUtil.randomFloat(Math.min(hSpeed.getValue(), hMult.getValue()) * 10f, Math.max(hSpeed.getValue(), hMult.getValue()) * 10f) / 180f;
	    float yawFov = (float) PlayerUtil.fovFromTarget(target);
	    float yawAdjustment = getSpeedRandomize(speedMode.getMode(), yawFov, yawOffset, hSpeed.getValue(), hMult.getValue());

	    float pitchOffset = MathUtil.randomFloat(Math.min(vSpeed.getValue(), vMult.getValue()) * 10f, Math.max(vSpeed.getValue(), vMult.getValue()) * 10f) / 90f;
	    float pitchEntity = (float) PlayerUtil.pitchFromTarget(target);
	    
	    float resultVertical = getSpeedRandomize(speedMode.getMode(), pitchEntity, pitchOffset, vSpeed.getValue(), vMult.getValue());

	    if (onTarget()) {
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
		lockedTargets.clear();
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
        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.entityHit == target;
    }
    
    private void applyYaw(float yawFov, float yawAdjustment) {
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

	private float getSpeedRandomize(String mode, float fov, float offset, float speed, float complement) {
	    float randomComplement = 0, result = 0;

	    switch (mode) {
	        case "Random":
	            randomComplement = MathUtil.randomFloat(complement / 100f, (complement + 0.5f) / 100f);
	            result = calculateResult(fov, offset, speed, MathUtil.randomFloat(speed, speed + 0.3f));
	            break;

	        case "Secure":
	            randomComplement = MathUtil.nextSecureFloat(complement / 100f, (complement + 0.5f) / 100f);
	            result = calculateResult(fov, offset, speed, MathUtil.nextSecureFloat(speed, speed + 0.3f));
	            break;

	        case "Gaussian":
	            randomComplement = (float) ((complement + MathUtil.randomGaussian(0.5f)) / 100f);
	            result = calculateResult(fov, offset, speed, (float) (speed + MathUtil.randomGaussian(0.3f)));
	            break;
	    }

	    return randomComplement + result;
	}
	
	private float normalizePitch(float pitch) {
	    return pitch >= 90f ? pitch - 360f : pitch <= -90f ? pitch + 360f : pitch;
	}

	private float calculateResult(float fov, float offset, float speed, float randomizedSpeed) {
	    return -fov * offset + fov / (100f - randomizedSpeed);
	}
	
	private boolean isYawFov(float fov) {
		return fov > 1.0f || fov < -1.0f;
	}
}
