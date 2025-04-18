package cc.unknown.module.impl.combat;

import java.util.Arrays;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.client.PatternUtil;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "AutoClicker", description = "Automatically clicks for you.", category = Category.COMBAT)
public class AutoClicker extends Module {
    
    private final SliderValue min = new SliderValue("MinCPS", this, 10, 1, 25);
    private final SliderValue max = new SliderValue("MaxCPS", this, 14, 1, 25);
    private final SliderValue amount = new SliderValue("Amount", this, 0.2f, 0, 1, 0.1f, () -> !this.randomization.is("Normal"));
    private final SliderValue boost = new SliderValue("Boost", this, 5, 1, 99, () -> this.conditionals.isEnabled("CPSBoost") && this.randomization.is("Normal"));
    
    private final ModeValue randomization = new ModeValue("Randomization", this, "Normal", "Normal", "Extra", "Extra+");
    
    public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
            new BoolValue("CPSBoost", false),
            new BoolValue("Inventory", false)));

    private final StopWatch stopWatch = new StopWatch();
    private long clickDelay = 0L;
        
    @Override
    public void onEnable() {
    	reset();
    }

    @Override
    public void onDisable() {
    	reset();
    }
    
    @SubscribeEvent
    public void onPostTick(ClientTickEvent event) {
    	if (event.phase == Phase.START) return;
    	correctValues(min, max);
    }
    
    @SubscribeEvent
    public void onRender3D(TickEvent.RenderTickEvent event) {
    	if (!isInGame()) return;
    	if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) return;
    	if (mc.currentScreen != null || !mc.inGameHasFocus) return;
    	if (!mc.gameSettings.keyBindAttack.isKeyDown()) return;

    	if (stopWatch == null) return;

    	ReflectUtil.setLeftClickCounter(0);

    	clickDelay = getClickDelay();

    	if (stopWatch.hasPassed(clickDelay)) {
    		PlayerUtil.leftClick(true);
    		stopWatch.reset();
    	}
    }

    @SubscribeEvent
    public void onPrePosition(PrePositionEvent event) {
        if (conditionals.isEnabled("Inventory") && mc.currentScreen instanceof GuiContainer) {
        	InventoryUtil.guiClicker(mc.currentScreen, 0, getClickDelay());
        }
    }

    public long getClickDelay() {
        return conditionals.isEnabled("CPSBoost") ? PatternUtil.randomization(randomization.getMode(), (int) MathUtil.randomizeDouble(min.getValue(), max.getValue()) + (int) boost.getValue(), amount.getValue()) : PatternUtil.randomization(randomization.getMode(), (int) MathUtil.randomizeDouble(min.getValue(), max.getValue()), amount.getValue());
    }
    
    private void reset() {
    	InventoryUtil.inventoryStopWatch.reset();
    	clickDelay = 0L;
    	stopWatch.reset();
    }
}