package cc.unknown.module.impl.utility;

import java.util.Arrays;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AutoTool", description = "Automatically selects the best tool or weapon from the player's inventory breaking blocks or attacking.", category = Category.UTILITY)
public class AutoTool extends Module {

    private final Slider delay = new Slider("Ticks", this, 0, 0, 10, 1);
    
    public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
            new Bool("SpoofSlot", true),
            new Bool("SwitchBack", true),
            new Bool("AutoSword", true),
            new Bool("RequireSneak", false)
    ));

    private int oldSlot;
    public boolean wasDigging;
    private final Clock delayTimer = new Clock();

    @Override
    public void onDisable() {
        if (wasDigging) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            wasDigging = false;
        }
        SpoofHandler.stopSpoofing();
    }

	@SubscribeEvent
	public void onAttack(AttackEvent event) {
        if (conditionals.isEnabled("AutoSword")) {
            if (!mc.thePlayer.isEating()) {
                InventoryUtil.bestSword(event.target);
            }
        }
    }

	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
    	if (event.phase == Phase.END) return;
		if (!isInGame()) return;
        if (!delayTimer.hasPassed(delay.getAsLong() * 50)) return;
        
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && 
            mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && 
            InventoryUtil.findTool(mc.objectMouseOver.getBlockPos()) != -1) {
            
            if (conditionals.isEnabled("RequireSneak") && !mc.gameSettings.keyBindSneak.isKeyDown()) return;
            
            if (!wasDigging) {
                oldSlot = mc.thePlayer.inventory.currentItem;
                if (conditionals.isEnabled("SpoofSlot")) {
                    SpoofHandler.startSpoofing(oldSlot);
                }
            }
            
            mc.thePlayer.inventory.currentItem = InventoryUtil.findTool(mc.objectMouseOver.getBlockPos());
            wasDigging = true;
            delayTimer.reset();
        } else if (wasDigging && (conditionals.isEnabled("SpoofSlot") || conditionals.isEnabled("SwitchBack"))) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            SpoofHandler.stopSpoofing();
            wasDigging = false;
        } else {
            oldSlot = mc.thePlayer.inventory.currentItem;
        }
    }
}