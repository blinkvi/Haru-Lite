package cc.unknown.module.impl.combat;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.move.MoveUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.Slider;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "MoreKB", description = "Amplifies knockback effect on opponents during combat.", category = Category.COMBAT)
public class MoreKB extends Module {
	
	private Mode mode = new Mode("Mode", this, "Normal", "Normal", "LegitFast", "Legit");

	private Slider delay = new Slider("Delay", this, 100, 0, 200, 5);
	private Slider chance = new Slider("Chance", this, 100, 0, 100, 1);
	private Slider threshold = new Slider("Threshold", this, 120, 0, 180, 1);
	private Slider distanceToEntity = new Slider("Distance", this, 4, 3, 10, 1);
	
	private Bool onlyGround = new Bool("OnlyGround", this, false);
	private Bool onlyMove = new Bool("OnlyMove", this, false);
	private Bool onlyMoveForward = new Bool("OnlyForward", this, false, onlyMove::get);
	private Bool ignoreTeammates = new Bool("IgnoreTeams", this, false);
	private Bool checkLiquids = new Bool("CheckLiquids", this, true);
	private Bool onlyWeapons = new Bool("OnlyWeapons", this, false);

	private EntityPlayer target;
	private final Clock clock = new Clock();
	private int ticks;
	
	@SubscribeEvent
	public void onAttack(AttackEvent event) {
		if (!MathUtil.chance(chance.getValue())) return;
		if (event.target instanceof IMob || event.target instanceof INpc) return;
		target = (EntityPlayer) event.target;
		
		if (mc.thePlayer == null || target == null) return;
		if (!clock.hasPassed(delay.getAsInt())) return;
		if (ignoreTeammates.get() && PlayerUtil.isTeam(target)) return;
		if (checkLiquids.get() && shouldJump()) return;
		if (PlayerUtil.unusedNames(target)) return;
		if (onlyWeapons.get() && !InventoryUtil.isSword()) return;
		if (onlyMove.get() && !MoveUtil.isMoving()) return;
		if (onlyMoveForward.get() && mc.thePlayer.movementInput.moveStrafe != 0f) return;
		if (onlyGround.get() && !mc.thePlayer.onGround) return;
		if (exceedsRotationThreshold(target)) return;
		if (mc.thePlayer.getDistanceToEntity(target) <= distanceToEntity.getAsFloat()) return;
		    
		if (clock.hasPassed(delay.getAsInt())) {
			clock.reset();
			ticks = 2;
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent ev) {
		switch (ticks) {
        case 2:
        	switch (mode.getMode()) {
                case "Normal":
                	ReflectUtil.setPressed(mc.gameSettings.keyBindForward, false);
                    break;
                case "LegitFast":
                	ReflectUtil.setServerSprintState(false);
                    break;
                case "Legit":
                    mc.thePlayer.setSprinting(false);
                    break;
            }
            ticks--;
        
            break;
        case 1: {
            switch (mode.getMode()) {
                case "Normal":
                	ReflectUtil.setPressed(mc.gameSettings.keyBindForward, GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
                    break;
                case "LegitFast":
                	ReflectUtil.setServerSprintState(true);
                    break;
                case "Legit":
                    mc.thePlayer.setSprinting(true);
                    break;
            }
            ticks--;
        }
            break;
        }
	}

	private boolean exceedsRotationThreshold(EntityPlayer target) {
	    double deltaX = mc.thePlayer.posX - target.posX;
	    double deltaZ = mc.thePlayer.posZ - target.posZ;
	    float calculatedYaw = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0 / Math.PI - 90.0);
	    float yawDifference = Math.abs(MathHelper.wrapAngleTo180_float(calculatedYaw - target.rotationYawHead));
	    return yawDifference > threshold.getAsFloat();
	}
	
	private boolean shouldJump() {
	    return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
	}
}
