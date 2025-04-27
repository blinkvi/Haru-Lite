package cc.unknown.module.impl.utility;

import java.util.Arrays;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.AttackEvent;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(name = "AutoTool", description = "Automatically selects the best tool or weapon from the player's inventory breaking blocks or attacking.", category = Category.UTILITY)
public class AutoTool extends Module {

    private final SliderValue delay = new SliderValue("Ticks", this, 0, 0, 10, 1);
    
    public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
            new BoolValue("SpoofSlot", true),
            new BoolValue("SwitchBack", true),
            new BoolValue("AutoSword", true),
            new BoolValue("RequireSneak", false)
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

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        if (conditionals.isEnabled("AutoSword")) {
            if (!mc.thePlayer.isEating()) {
                InventoryUtil.bestSword(event.target);
            }
        }
    };

    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
		if (!PlayerUtil.isInGame()) return;
        if (!delayTimer.hasPassed((long) (delay.getValue() * 50))) return;
        
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
    };
}