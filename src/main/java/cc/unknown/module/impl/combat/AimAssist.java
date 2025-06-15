package cc.unknown.module.impl.combat;
import java.util.Arrays;
import java.util.List;

import akka.japi.Pair;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.RotationUtil;
import cc.unknown.util.structure.vectors.Vec3;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {
	
	private final Slider horizontal = new Slider("HorizontalSpeed", this, 5, 1, 20, 0.1);
	private final Bool verticalCheck = new Bool("Vertical", this, false);
	private final Slider vertical = new Slider("VerticalSpeed", this, 5, 1, 20, 0.1, verticalCheck::get);
	
	private final Slider fov = new Slider("Fov", this, 0, 0, 360);
	
	private final Slider range = new Slider("Range", this, 3.5, 0.1, 8, 0.1);
	
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("RequireClick", true),
			new Bool("LockTarget", true),
			new Bool("IncreaseStrafe", true),
			new Bool("CheckBlockBreak", true),
			new Bool("IgnoreTeams", true),
			new Bool("VisibilityCheck", true),
			new Bool("WeaponsOnly", true)));

    private Double yawNoise = null;
    private Double pitchNoise = null;
    private long nextNoiseRefreshTime = -1;
    private long nextNoiseEmptyTime = 200;
    
    private final Clock clock = new Clock();

    @Override
    public void onDisable() {
        yawNoise = pitchNoise = null;
        nextNoiseRefreshTime = -1;
    }
    
    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (noAction()) {
            return;
        }

        final EntityPlayer target = getEnemy();
        if (target == null) return;
        final boolean onTarget = mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
                && mc.objectMouseOver.entityHit == target;

        double deltaYaw = yawNoise;
        double deltaPitch = pitchNoise;

        double hSpeed = horizontal.getValue() + 10;
        double vSpeed = vertical.getValue() + 10;


        if (onTarget) {
            if (conditionals.isEnabled("LockTarget")) {
                hSpeed *= 0.85;
                vSpeed *= 0.85;
            } else {
                hSpeed = 0;
                vSpeed = 0;
            }
        }

        if (conditionals.isEnabled("IncreaseStrafe")) {
            int mouseX = Math.abs(mc.mouseHelper.deltaX);
            int mouseY = Math.abs(mc.mouseHelper.deltaY);

            if (mouseX > 100)
                hSpeed = 0;
            else
                hSpeed = Math.min(hSpeed, (100 - mouseX) / 35.0);

            if (mouseY > 100)
                vSpeed = 0;
            else
                vSpeed = Math.min(hSpeed, (100 - mouseY) / 35.0);
        }

        final Pair<Pair<Float, Float>, Pair<Float, Float>> rotation = RotationUtil.getRotation(target.getEntityBoundingBox());
        final Pair<Float, Float> yaw = rotation.first();
        final Pair<Float, Float> pitch = rotation.second();

        boolean move = false;

        final float curYaw = mc.thePlayer.rotationYaw;
        final float curPitch = mc.thePlayer.rotationPitch;
        if (yaw.first() > curYaw) {
            move = true;
            final float after = rotMove(yaw.first(), curYaw, (float) hSpeed);
            deltaYaw += after - curYaw;
        } else if (yaw.second() < curYaw) {
            move = true;
            final float after = rotMove(yaw.second(), curYaw, (float) hSpeed);
            deltaYaw += after - curYaw;
        }
        
        if (verticalCheck.get()) {
            if (pitch.first() > curPitch) {
                move = true;
                final float after = rotMove(pitch.first(), curPitch, (float) vSpeed);
                deltaPitch += after - curPitch;
            } else if (pitch.second() < curPitch) {
                move = true;
                final float after = rotMove(pitch.second(), curPitch, (float) vSpeed);
                deltaPitch += after - curPitch;
            }
        }

        if (move) {
            deltaYaw += (Math.random() - 0.5) * Math.min(0.8, deltaPitch / 10.0);
            deltaPitch += (Math.random() - 0.5) * Math.min(0.8, deltaYaw / 10.0);
        }

        mc.thePlayer.rotationYaw += deltaYaw;
        mc.thePlayer.rotationPitch += deltaPitch;
    }
    
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        long time = System.currentTimeMillis();
        if (nextNoiseRefreshTime == -1 || time >= nextNoiseRefreshTime + nextNoiseEmptyTime) {
            nextNoiseRefreshTime = (long) (time + Math.random() * 60 + 80);
            nextNoiseEmptyTime = (long) (Math.random() * 100 + 180);
            yawNoise = (Math.random() - 0.5) * 2 * ((Math.random() - 0.5) * 0.3 + 0.8);
            pitchNoise = (Math.random() - 0.5) * 2 * ((Math.random() - 0.5) * 0.35 + 0.6);
        } else if (time >= nextNoiseRefreshTime) {
            yawNoise = 0d;
            pitchNoise = 0d;
        }
    }
    
    private EntityPlayer getEnemy() {
        final List<EntityPlayer> players = mc.theWorld.playerEntities;
        final Vec3 playerPos = new Vec3(mc.thePlayer);

        EntityPlayer target = null;
        double targetFov = Double.MAX_VALUE;
        for (final EntityPlayer entityPlayer : players) {
            if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {
                double dist = playerPos.distanceTo(entityPlayer);
                if (FriendUtil.isFriend(entityPlayer)) continue;
                if (conditionals.isEnabled("IgnoreTeams") && PlayerUtil.isTeam(entityPlayer)) continue;
                if (dist > range.getValue()) continue;
                if (fov.getAsInt() != 360 && !PlayerUtil.fov(fov.getAsInt(), entityPlayer)) continue;
                if (conditionals.isEnabled("VisibilityCheck") && !mc.thePlayer.canEntityBeSeen(entityPlayer)) continue;
                double curFov = Math.abs(PlayerUtil.getFov(entityPlayer.posX, entityPlayer.posZ));
                if (curFov < targetFov) {
                    target = entityPlayer;
                    targetFov = curFov;
                }
            }
        }
        return target;
    }

    private float rotMove(float target, float current, float diff) {
        float delta;
        if (target > current) {
            float dist1 = target - current;
            float dist2 = current + 360 - target;
            if (dist1 > dist2) {
                delta = -current - 360 + target;
            } else {
                delta = dist1;
            }
        } else if (target < current) {
            float dist1 = current - target;
            float dist2 = target + 360 - current;
            if (dist1 > dist2) {
                delta = current + 360 + target;
            } else {
                delta = -dist1;
            }
        } else {
            return current;
        }

        delta = normalize(delta, -180, 180);

        if (Math.abs(delta) < 0.1 * Math.random() + 0.1) {
            return current;
        } else if (Math.abs(delta) <= diff) {
            return current + delta;
        } else {
            if (delta < 0) {
                return current - diff;
            } else if (delta > 0) {
                return current + diff;
            } else {
                return current;
            }
        }
    }
    
    private float normalize(float yaw, float min, float max) {
        yaw %= 360.0F;
        if (yaw >= max) {
            yaw -= 360.0F;
        }
        if (yaw < min) {
            yaw += 360.0F;
        }

        return yaw;
    }
    
    private boolean noAction() {
        if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
        if (conditionals.isEnabled("WeaponsOnly") && !InventoryUtil.isSword()) return true;
        if (yawNoise == null || pitchNoise == null) return true;
        if (mc.gameSettings.keyBindAttack.isKeyDown()) clock.reset();
        if (conditionals.isEnabled("RequireClick") && (clock.hasPassed(150) || !mc.thePlayer.isSwingInProgress)) return true;
        return conditionals.isEnabled("CheckBlockBreak") && ReflectUtil.isHittingBlock();
    }
}
