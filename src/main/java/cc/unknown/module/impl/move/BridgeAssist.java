package cc.unknown.module.impl.move;

import java.util.Arrays;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.player.MoveInputEvent;
import cc.unknown.event.player.PlaceEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.utility.AutoTool;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.client.ChatUtil;
import cc.unknown.util.structure.vectors.Vector2d;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BridgeAssist", description = "Automatically sneaks for you when you are near the edge of a block.", category = Category.MOVE)	
public class BridgeAssist extends Module {
    
	private final SliderValue edge = new SliderValue("Edge", this, 0.15f, 0.01f, 0.30f, 0.01f);
    
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("RequireSneak", false),
			new BoolValue("BlockSwitching", false),
			new BoolValue("SneakOnJump", false),
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
    	if (isShifting && shouldBridge) event.sneak = true;
    	if (!isShifting && shouldBridge) event.sneak = false;
    }

	@SubscribeEvent
	public void onPlace(PlaceEvent event) {		
		if (isShifting) {
			isShifting = false;
		}
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

        boolean legit = mc.thePlayer.onGround || (conditionals.isEnabled("SneakOnJump") && !mc.thePlayer.onGround);

        if (legit) {
        	if (shouldSneak()) {
        		isShifting = true;
        		shouldBridge = true;
        	} else if (conditionals.isEnabled("RequireSneak")) {
        		boolean sneakKey = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        		isShifting = false;
        		shouldBridge = sneakKey;
        	} else {
        		isShifting = false;
        		shouldBridge = false;
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
        EntityPlayerSP player = mc.thePlayer;
        double dist = 0.6D;
        double offset = edge.getValue() + 0.01;
        ChatUtil.display(offset);
        double motionY = player.motionY;
        AxisAlignedBB box = player.getEntityBoundingBox();
        
        boolean airBelow = PlayerUtil.checkAir();
        boolean airAround = Stream.of(new Vector2d(offset, 0.0D), new Vector2d(-offset, 0.0D), new Vector2d(0.0D, offset), new Vector2d(0.0D, -offset), new Vector2d(offset, offset), new Vector2d(offset, -offset), new Vector2d(-offset, offset), new Vector2d(-offset, -offset)).anyMatch(dir -> {
        	AxisAlignedBB expandedBox = box.offset(dir.x, -0.01, dir.y);
            return mc.theWorld.getCollidingBoundingBoxes(player, expandedBox).isEmpty();
        });

        if (airBelow | airAround) {
            if (offset < dist && offset >= -dist) {
                offset = 0.0D;
            } else {
                offset += (offset > 0.0D ? -dist : dist);
            }
        }

        if (mc.theWorld.getCollidingBoundingBoxes(player, box.offset(0.0D, motionY, 0.0D)).isEmpty()) {
            if (motionY < dist && motionY >= -dist) {
                motionY = 0.0D;
            } else {
                motionY += (motionY > 0.0D ? -dist : dist);
            }
        }

        return offset == 0.0D || motionY == 0.0D;
    }
}