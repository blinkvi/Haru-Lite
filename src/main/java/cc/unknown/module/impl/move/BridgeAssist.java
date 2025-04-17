package cc.unknown.module.impl.move;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.player.MoveInputEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.mixin.impl.IEntityPlayer;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.utility.AutoTool;
import cc.unknown.util.client.system.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BridgeAssist", description = "Automatically sneaks for you when you are near the edge of a block.", category = Category.MOVE)	
public class BridgeAssist extends Module {
    
	private final SliderValue delay = new SliderValue("Ticks", this, 1, 0, 5, 1);
	private final SliderValue edgeOffset = new SliderValue("EdgeOffset", this, 0.3f, 0f, 0.4f, 0.01f);
    private final SliderValue pitch = new SliderValue("Angle", this, 45, 0, 90, 5, () -> this.conditionals.isEnabled("AngleCheck"));
    
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("AngleCheck", false),
			new BoolValue("Legitimize", false),
			new BoolValue("RequireSneak", false),
			new BoolValue("BlockSwitching", false),
			new BoolValue("OnlyBlocks", true),
			new BoolValue("OnlyBackwards", false)));

    private StopWatch stopWatch = new StopWatch();
    private boolean shouldBridge = false, isShifting = false;
    private boolean checkAir;
    private int slot;
    
    @Override
    public void onEnable() {
        if (conditionals.isEnabled("BlockSwitching")) slot = -1;
    }

    @Override
    public void onDisable() {
    	reset();
        if (checkAir) reset();
        if (conditionals.isEnabled("BlockSwitching")) mc.thePlayer.inventory.currentItem = slot;
    }
    
    @SubscribeEvent
    public void onMoveInput(MoveInputEvent event) {
    	if (isShifting && shouldBridge) {
    		((IEntityPlayer) mc.thePlayer).getHideSneakHeight().reset();
    		event.setSneak(true);
    	}
    	
    	if (!isShifting && !shouldBridge) {
    		event.setSneak(false);
    	}
    	
    	if (!isShifting && shouldBridge) {
    		event.setSneak(false);
    	}
    	
    	if (!isShifting) {
    		event.setSneak(false);
    	}    	
    }
    
    @SubscribeEvent
    public void onPreAttack(PrePositionEvent event) {
    	if (noBridge()) return;
    	 
        boolean shift = delay.getValue() > 0;
        checkAir = conditionals.isEnabled("Legitimize") && PlayerUtil.checkAir();
        
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
			if (PlayerUtil.checkEdge(edgeOffset.getValue(), 0) || checkAir) {
				if (shift) {
				    stopWatch.setMillis((int) (delay.getValue() * 50));
				    stopWatch.reset();
				}

				isShifting = true;
				shouldBridge = true;
			} 
			
			else if (mc.thePlayer.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && conditionals.isEnabled("RequireSneak")) {
				isShifting = false;
				shouldBridge = false;
			}
			
			else if (conditionals.isEnabled("RequireSneak") && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				isShifting = false;
				shouldBridge = false;
			}
			
			else if (mc.thePlayer.isSneaking() && (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && conditionals.isEnabled("RequireSneak")) && (!shift || stopWatch.finished())) {
				isShifting = false;
				shouldBridge = true;
			}
			
			else if (mc.thePlayer.isSneaking() && !conditionals.isEnabled("RequireSneak") && (!shift || stopWatch.finished())) {
				isShifting = false;
				shouldBridge = true;
			}
		}
		
		else if (shouldBridge && (PlayerUtil.checkEdge(edgeOffset.getValue(), 0) || checkAir)) {
			isShifting = true;
		}
		
		else {
			isShifting = false;
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
    
    private void reset() {
        PlayerUtil.setShift(false);
        stopWatch.reset();
    }
    
    // pitch 78
    // yaw 225
    // diagonal
    
    // pitch 77.2
    // yaw 45
    // forward
}
