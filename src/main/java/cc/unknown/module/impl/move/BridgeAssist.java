package cc.unknown.module.impl.move;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.player.MoveInputEvent;
import cc.unknown.event.player.PlaceEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.utility.AutoTool;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BridgeAssist", description = "Automatically sneaks for you when you are near the edge of a block.", category = Category.MOVE)	
public class BridgeAssist extends Module {
    
    private final SliderValue pitch = new SliderValue("Angle", this, 45, 0, 90, 5, () -> this.conditionals.isEnabled("AngleCheck"));
    
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("AngleCheck", false),
			new BoolValue("RequireSneak", false),
			new BoolValue("BlockSwitching", false),
			new BoolValue("OnlyBlocks", true),
			new BoolValue("OnlyBackwards", false)));

    private boolean shouldBridge = false, isShifting = false;
    private int slot;
    
    @Override
    public void onEnable() {
        if (conditionals.isEnabled("BlockSwitching")) slot = -1;
    }

    @Override
    public void onDisable() {
        if (conditionals.isEnabled("BlockSwitching")) mc.thePlayer.inventory.currentItem = slot;
    }
    
    @SubscribeEvent
    public void onMoveInput(MoveInputEvent event) {
    	if (isShifting && shouldBridge) {
    		event.sneak = true;
    	}
    	
    	if (!isShifting && shouldBridge) {
    		event.sneak = false;
    	}
    }

	@SubscribeEvent
	public void onPlace(PlaceEvent event) {
		isShifting = false;
	}
    
    @SubscribeEvent
    public void onPreMotion(PrePositionEvent event) {
    	if (noBridge()) return;
    	         
        if (conditionals.isEnabled("RequireSneak")) {
            if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
               shouldBridge = false;
               return;
            }
         }
        
		if (conditionals.isEnabled("OnlyBlocks")) {
			if (!InventoryUtil.getAnyBlock()) {
				if (isShifting) {
					isShifting = false;
				}
				return;
			}
		}
        
        if (conditionals.isEnabled("OnlyBackwards") && (mc.thePlayer.movementInput.moveForward > 0) && (mc.thePlayer.movementInput.moveStrafe == 0) || mc.thePlayer.movementInput.moveForward >= 0) {
            shouldBridge = false;
            isShifting = false;
            return;
        }
        
		if (conditionals.isEnabled("CheckAngle") && mc.thePlayer.rotationPitch < pitch.getValue()) {
			isShifting = false;
			return;
		}

		if (mc.thePlayer.onGround) {
			if (shouldSneak()) {
				isShifting = true;
				shouldBridge = true;
			} else if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && conditionals.isEnabled("RequireSneak")) {
				isShifting = false;
				shouldBridge = false;
			} else if ((Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && conditionals.isEnabled("RequireSneak"))) {
				isShifting = false;
				shouldBridge = true;
			}
		} else {
			isShifting = false;
			shouldBridge = false;
		}
    }

	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
        if (!isInGame()) return;
        
        if (conditionals.isEnabled("BlockSwitching") && slot == -1) {
            slot = mc.thePlayer.inventory.currentItem;
        }
        
        int slot = InventoryUtil.findBlock();
        
        if (slot == -1) return;
        
        if (conditionals.isEnabled("BlockSwitching") && !InventoryUtil.getAnyBlock()) {
            mc.thePlayer.inventory.currentItem = slot;
        }
        
        if (mc.currentScreen == null || mc.thePlayer.getHeldItem() == null) return;
    }
    
    private boolean noBridge() {
        if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) return true;
        if (mc.thePlayer.capabilities.isFlying) return true;
        if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
        if (getModule(AutoTool.class).isEnabled() && getModule(AutoTool.class).wasDigging) return true;
    	return false;
    }

	private boolean shouldSneak() {
		double d6 = 0.05D;
		double x = mc.thePlayer.motionX;
		double z = mc.thePlayer.motionZ;

		if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x, -1.0D, 0.0D)).isEmpty()) {
			if (x < d6 && x >= -d6) {
				x = 0.0D;
			} else if (x > 0.0D) {
				x -= d6;
			} else {
				x += d6;
			}
		}

		if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -1.0D, z)).isEmpty()) {
			if (z < d6 && z >= -d6) {
				z = 0.0D;
			} else if (z > 0.0D) {
				z -= d6;
			} else {
				z += d6;
			}
		}

		if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x, -1.0D, z)).isEmpty()) {
			if (x < d6 && x >= -d6) {
				x = 0.0D;
			} else if (x > 0.0D) {
				x -= d6;
			} else {
				x += d6;
			}

			if (z < d6 && z >= -d6) {
				z = 0.0D;
			} else if (z > 0.0D) {
				z -= d6;
			} else {
				z += d6;
			}
		}
		return x == 0 || z == 0;
	}
    
    // pitch 78
    // yaw 225
    // diagonal
    
    // pitch 77.2
    // yaw 45
    // forward
}