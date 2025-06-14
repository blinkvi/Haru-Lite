package cc.unknown.module.impl.combat;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {

	private final Mode mode = new Mode("Mode", this, "Regular", "Regular", "Lock");
	
	private final Slider hSpeed = new Slider("HorizontalSpeed", this, 3.4f, 0.1f, 20, 0.01f, () -> mode.is("Regular"));
	
	private Bool vertical = new Bool("Vertical", this, false);
	private Slider vSpeed = new Slider("VerticalSpeed", this, 2.1f, 0.1f, 10, 0.01f, () -> mode.is("Regular") && vertical.get());
	
	private final Slider angle = new Slider("Angle", this, 180, 0, 180, 1);
	private final Slider distance = new Slider("Distance", this, 4f, 1f, 8f, 0.1f);

	private final Mode speedMode = new Mode("Speed", this, () -> mode.is("Regular"), "Random", "Random", "Secure", "Gaussian");
	
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("RequireClicking", true),
			new Bool("LockTarget", false),
			new Bool("IgnoreFriends", false),
			new Bool("IgnoreTeams", false),
			new Bool("IgnoreInvisibles", false),
			new Bool("VisibilityCheck", true),
			new Bool("CheckBlockBreak", false),
			new Bool("WeaponsOnly", false)));
	
	private final Clock clock = new Clock();
	public EntityPlayer target;
	
	@Override
	public void onDisable() {
		target = null;
	}
	
	@SubscribeEvent
	public void onAttack(AttackEvent event) {
		if (conditionals.isEnabled("LockTarget")) {
	    	if (target == null) {
	    		target = (EntityPlayer) event.target;
	    	}
		}
	}
	
	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
	    if (event.phase == Phase.START || !isInGame() || mc.currentScreen != null || !mc.inGameHasFocus || target == null) return;

	    if (conditionals.isEnabled("WeaponsOnly") && !InventoryUtil.isSword()) return;

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

	    if (!conditionals.isEnabled("LockTarget") || target == null) {
	        target = getEnemy();
	    }
	    
	    switch (mode.getMode()) {
	    case "Lock":
	    	RotationUtil.getLockRotation(target, vertical.get());
	    	break;
	    case "Regular":
	        double yawFov = PlayerUtil.fovFromTarget(target);
	        double pitchToTarget = PlayerUtil.pitchFromTarget(target);

	        double yawOffset = MathUtil.randomDouble(0.85, 1.15) * hSpeed.getValue();
	        double yawAdjustment = getSpeedRandomize(speedMode.getMode(), yawFov, yawOffset, hSpeed.getValue(), hSpeed.getValue());

	        double pitchOffset = MathUtil.randomDouble(0.85, 1.15) * vSpeed.getValue();
	        double pitchAdjustment = getSpeedRandomize(speedMode.getMode(), pitchToTarget, pitchOffset, vSpeed.getValue(), vSpeed.getValue());

	        applyYaw(yawFov, yawAdjustment);
	        
		    if (vertical.get()) {
		        mc.thePlayer.rotationPitch = normalizePitch((float) pitchAdjustment);
		    }
	        break;
	    }
	}

	public EntityPlayer getEnemy() {
	    int fov = (int) angle.getValue();
	    EntityPlayer bestTarget = null;
	    double bestScore = Double.MAX_VALUE;

	    for (EntityPlayer player : mc.theWorld.playerEntities) {
	        if (!isValidTarget(player, fov)) continue;

	        AxisAlignedBB bb = player.getEntityBoundingBox();
	        double score = RotationUtil.nearestRotation(bb);

	        if (score < bestScore) {
	            bestScore = score;
	            bestTarget = player;
	        }
	    }

	    return bestTarget;
	}

	
	private boolean isValidTarget(EntityPlayer player, int fov) {
		if (player == mc.thePlayer || !player.isEntityAlive()) return false;
	    if (PlayerUtil.unusedNames(player)) return false;
	    if (!conditionals.isEnabled("IgnoreInvisibles") && player.isInvisible()) return false;
	    if (FriendUtil.isFriend(player) && conditionals.isEnabled("IgnoreFriends")) return false;
	    if (conditionals.isEnabled("IgnoreTeams") && PlayerUtil.isTeam(player)) return false;
	    if (mc.thePlayer.getDistanceToEntity(player) > distance.getValue()) return false;
	    if (conditionals.isEnabled("VisibilityCheck") && !mc.thePlayer.canEntityBeSeen(player)) return false;
	    return fov == 180 || PlayerUtil.fov(fov, player);
	}

    private void applyYaw(double yawFov, double yawAdjustment) {
        if (isYawFov(yawFov)) {
            mc.thePlayer.rotationYaw += yawAdjustment;
        }
    }

	private double getSpeedRandomize(String mode, double fov, double offset, double speed, double complement) {
	    return Stream.of(
	        new AbstractMap.SimpleEntry<>("Random", (Supplier<Double>) () -> {
	            double randComp = MathUtil.randomDouble(complement / 100, (complement + 0.2) / 100);
	            double randSpeed = MathUtil.randomDouble(speed, speed + 0.3);
	            return randComp + calculateResult(fov, offset, speed, randSpeed);
	        }),
	        new AbstractMap.SimpleEntry<>("Secure", (Supplier<Double>) () -> {
	            double randComp = MathUtil.nextSecureDouble(complement / 100, (complement + 0.5) / 180);
	            double randSpeed = MathUtil.nextSecureDouble(speed, speed + 0.3);
	            return randComp + calculateResult(fov, offset, speed, randSpeed);
	        }),
	        new AbstractMap.SimpleEntry<>("Gaussian", (Supplier<Double>) () -> {
	            double randComp = (complement + MathUtil.randomGaussian(0.15)) / 100;
	            double randSpeed = MathUtil.lerpDouble(speed, speed + MathUtil.randomGaussian(0.15), 0.5);
	            double result = randComp + calculateResult(fov, offset, speed, randSpeed);
	            return result;
	        })).filter(entry -> entry.getKey().equalsIgnoreCase(mode)).map(entry -> entry.getValue().get()).findFirst().orElse(speed);
	}
	
	private float normalizePitch(float pitch) {
	    return pitch >= 90f ? pitch - 360f : pitch <= -90f ? pitch + 360f : pitch;
	}

	private double calculateResult(double fov, double offset, double speed, double randomizedSpeed) {
	    return fov - (fov * offset * ((speed + randomizedSpeed) / 200.0));
	}
	
	private boolean isYawFov(double fov) {
		return fov > 1.0f || fov < -1.0f;
	}
}
