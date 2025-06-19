package cc.unknown.module.impl.combat;
import org.lwjgl.input.Mouse;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.math.MathUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AimAssist", description = "Assists with aiming at opponents in a legitimate manner.", category = Category.COMBAT)
public class AimAssist extends Module {
	
	private final Slider horizontal = new Slider("Horizontal", this, 60, 10, 100, 1);
	private final Slider distance = new Slider("Distance", this, 4.5, 1, 10, 0.5);
	private final Bool clickAim = new Bool("ClickAim", this, true);
	private final Bool weaponOnly = new Bool("WeaponOnly", this, false);
	private final Bool aimInvis = new Bool("AimAtInvis", this, false);
	private final Bool breakBlocks = new Bool("BreakBlocks", this, true);
	
	public boolean breakHeld = false;
	private final Clock clock = new Clock();
	
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
    	if (event.phase == Phase.END) {
    	    if (mc.gameSettings.keyBindAttack.isKeyDown()) clock.reset();

            if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus || (clickAim.get() && (clock.hasPassed(150) || !mc.thePlayer.isSwingInProgress)) || (weaponOnly.get() && InventoryUtil.isSword()) || (breakBlocks.get() && breakBlock())) return;

            Entity en = this.getEnemy();
            if (en != null) {
                double n = n(en);
                if (n > 1 || n < -1) {
                    float val = (float)(-(MathUtil.randomDouble(horizontal.getValue() - 1.47328, horizontal.getValue() + 2.48293) / 100 + n / (101 - MathUtil.randomDouble(horizontal.getValue() - 4.723847, horizontal.getValue()))));
                    float strafe = mc.thePlayer.moveStrafing;
                    if (strafe != 0) val += -strafe * 0.03f;

                    mc.thePlayer.rotationYaw += val;
                }
            }
    	}
    }
    
    public Entity getEnemy() {
        for (EntityPlayer en : mc.theWorld.playerEntities) {
            if (!isTarget(en)) {
                continue;
            } else if (!aimInvis.get() && en.isInvisible()) {
                continue;
            } else if ((double) mc.thePlayer.getDistanceToEntity(en) > distance.getAsFloat()) {
                continue;
            }
            return en;
        }
        return null;
    }

    public static boolean fov(Entity entity, float fov) {
        fov = (float) ((double) fov * 0.5D);
        double v = ((double) (mc.thePlayer.rotationYaw - m(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
        return v > 0.0D && v < (double) fov || (double) (-fov) < v && v < 0.0D;
    }

    public static double n(Entity en) {
        return ((double) (mc.thePlayer.rotationYaw - m(en)) % 360.0D + 540.0D) % 360.0D - 180.0D;
    }

    public static float m(Entity ent) {
        double x = ent.posX - mc.thePlayer.posX;
        double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float) (yaw * -1.0D);
    }

    public boolean breakBlock() {
        if (breakBlocks.get() && mc.objectMouseOver != null) {
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

    public boolean isTarget(EntityPlayer en) {
        if (en == mc.thePlayer)
            return false;
        return en.deathTime == 0;
    }
}
