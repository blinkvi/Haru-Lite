package cc.unknown.module.impl.combat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.RotationUtil;
import cc.unknown.util.structure.vectors.Vector2f;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {
	
	private final Slider horizontal = new Slider("HorizontalSpeed", this, 60, 10, 100, 1);
	private final Bool vertically = new Bool("Vertical", this, false);
	private final Slider fieldOfView = new Slider("FOV", this, 90, 15, 360, 1);
	private final Slider distance = new Slider("Distance", this, 4.5, 1, 10, 0.5);
	
	public final MultiBool c = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("ClickAim", true),
			new Bool("WeaponOnly", false),
			new Bool("ViewCheck", false),
			new Bool("AimInvisibles", false),
			new Bool("BreakBlocks", true),
			new Bool("InstantAim", false),
			new Bool("IgnoreTeams", false),
			new Bool("IgnoreFriends", false)));
	
	public boolean breakHeld = false;
	private final Clock clock = new Clock();
	private final Random random = new Random();
	private ArrayList<Entity> teams = new ArrayList<>();
	
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
    	if (!isInGame()) return;
    	
    	if (event.phase == Phase.END) {

            if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus) return;
    	    if (mc.gameSettings.keyBindAttack.isKeyDown()) clock.reset();
    	    if (c.isEnabled("ClickAim") && (clock.hasPassed(150) || !mc.thePlayer.isSwingInProgress)) return;
    	    if (c.isEnabled("BreakBlocks") && breakBlock()) return;
    	    if (c.isEnabled("WeaponOnly") && !InventoryUtil.isSword()) return;
    	    
            EntityPlayer target = getEnemy();
			Vector2f rotations = RotationUtil.getAngles(target);

            if (target == null) return;

			if (c.isEnabled("InstantAim")) {
				RotationUtil.getLockRotation(target, true);
			}
			
			double fov = fovFromEntity(target);
			
			if (fov > 1 || fov < -1) {
				double speed = fov * (ThreadLocalRandom.current().nextDouble(horizontal.getValue() - 1.47328, horizontal.getValue() + 2.48293) / 100);
				float finalSpeed = (float) (-(speed + fov / (101.0D - (float) ThreadLocalRandom.current().nextDouble(horizontal.getValue() - 4.723847, horizontal.getValue()))));
				
				float strafe = mc.thePlayer.moveStrafing;
                if (strafe != 0) finalSpeed += -strafe * 0.03f;
                mc.thePlayer.rotationYaw += finalSpeed;
			}
        	
			if (vertically.get()) {
				float randomPitch = random.nextBoolean() ? -RandomUtils.nextFloat(1.2F, 2f) : RandomUtils.nextFloat(0F, 2f);
				mc.thePlayer.rotationPitch = rotations.y + randomPitch + -15;

			}

    	}
    }
    
    public EntityPlayer getEnemy() {
		int fov = fieldOfView.getAsInt();
		List<EntityPlayer> playerList = new ArrayList<>(mc.theWorld.playerEntities);

	    playerList.sort(new Comparator<EntityPlayer>() {
	        @Override
	        public int compare(EntityPlayer player1, EntityPlayer player2) {
                double distance1 = mc.thePlayer.getDistanceToEntity(player1);
                double distance2 = mc.thePlayer.getDistanceToEntity(player2);
                int distanceComparison = Double.compare(distance1, distance2);
                return distanceComparison;
	        }
	    });

		for (final EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
			if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0 && !entityPlayer.isDead) {

				if (c.isEnabled("IgnoreFriends") && FriendUtil.isFriend(entityPlayer)) {
					continue;
				}

				if (c.isEnabled("ViewCheck") && !mc.thePlayer.canEntityBeSeen(entityPlayer)) {
					continue;
				}

				if (c.isEnabled("IgnoreTeams") && !isTeam(entityPlayer)) {
					continue;
				}

				if (!c.isEnabled("AimInvisibles") && entityPlayer.isInvisible()) {
					continue;
				}

				if (mc.thePlayer.getDistanceToEntity(entityPlayer) > distance.getAsFloat()) {
					continue;
				}

				if (!c.isEnabled("InstantAim") && fov != 360 && isWithinFOV(entityPlayer, fov)) {
					continue;
				}

				return entityPlayer;
			}
		}

		return null;
	}

	private boolean isWithinFOV(EntityPlayer player, int fieldOfView) {
		return PlayerUtil.fov(fieldOfView, player);
	}

    private boolean breakBlock() {
        if (mc.objectMouseOver != null) {
            BlockPos p = mc.objectMouseOver.getBlockPos();
            if (p != null && Mouse.isButtonDown(0)) {
                if (mc.theWorld.getBlockState(p).getBlock() != Blocks.air
                        && !(mc.theWorld.getBlockState(p).getBlock() instanceof BlockLiquid)) {
                    if (!breakHeld) {
                        int e = mc.gameSettings.keyBindAttack.getKeyCode();
                        KeyBinding.setKeyBindState(e, true);
                        KeyBinding.onTick(e);
                        breakHeld = true;
                    }
                    return true;
                }
                if (breakHeld) {
                    breakHeld = false;
                }
            }
        }
        return false;
    }
    
    private double fovFromEntity(EntityPlayer en) {
        return ((((double) (mc.thePlayer.rotationYaw - calculate(en)) % 360.0D) + 540.0D) % 360.0D) - 180.0D;
    }
    
    private boolean isTeam(Entity entity) {
    	if(entity == mc.thePlayer) return true;
    	for (Entity ent : teams) {
    		if (ent.equals(entity))
              return true;
    	}
    	try {
    		EntityPlayer player = (EntityPlayer) entity;
    		if(mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName().getUnformattedText().startsWith(player.getDisplayName().getUnformattedText().substring(0, 2))) return true;
    	} catch (Exception fhwhfhwe) { }
    	return false;
     }

    
    private float calculate(EntityPlayer ent) {
        double x = ent.posX - mc.thePlayer.posX;
        double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float) (yaw * -1.0D);
    }
}
