package cc.unknown.module.impl.utility;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Sets;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PrePositionEvent;
import cc.unknown.event.impl.RenderWorldLastEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

@ModuleInfo(name = "FastPlace", description = "Autoclicker for rightclicking.", category = Category.UTILITY)
public class FastPlace extends Module {


    private final SliderValue cps = new SliderValue("CPS", this, 10, 1, 25);
    private final SliderValue deltaCPS = new SliderValue("DeltaCPS", this, 2, 0, 20);
    private final SliderValue delay = new SliderValue("StartDelay", this, 0, 0, 200, 10);

    public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
            new BoolValue("OnlyBlocks", true),
            new BoolValue("NoObsidian", true),
            new BoolValue("Projectiles", false), 
            new BoolValue("Inventory", false),
            new BoolValue("OnlyClick", true)));

    private final Clock stopWatch = new Clock();
    private final Clock startDelay = new Clock();
    
    private final Set<Item> BLACKLISTED_ITEMS = Sets.newHashSet(Items.compass, Items.clock, Items.ender_pearl, Items.fishing_rod, Items.stone_sword, Items.diamond_sword, Items.golden_sword, Items.iron_sword, Items.wooden_sword, Items.nether_star, Items.emerald, Items.cake, Items.skull);
    private final Set<Block> BLACKLISTED_BLOCKS = Sets.newHashSet(Blocks.obsidian);
    
    @Override
    public void onEnable() {
    	reset();
    }

    @Override
    public void onDisable() {
    	reset();
    }

    @EventLink
    public final Listener<PrePositionEvent> onPrePosition = event -> {
        if (conditionals.isEnabled("Inventory") && mc.currentScreen instanceof GuiContainer) {
        	InventoryUtil.guiClicker(mc.currentScreen, 1, 1000 / getRandomizedCPS());
        }
    };
    
    @EventLink
    public final Listener<RenderWorldLastEvent> onRender3D = event -> {
		if (!PlayerUtil.isInGame()) return;

        if (mc.currentScreen != null || !mc.inGameHasFocus) return;
		if (BLACKLISTED_ITEMS.contains(InventoryUtil.getItem())) return;
		
    	if (!startDelay.hasPassed((int) delay.getValue())) return;

        if (conditionals.isEnabled("Inventory") && mc.currentScreen instanceof GuiInventory && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return;
        if (!conditionals.isEnabled("Projectiles") && InventoryUtil.getProjectiles()) return;
        if (!conditionals.isEnabled("OnlyBlocks") && InventoryUtil.getAnyBlock()) return;
        
        if (conditionals.isEnabled("NoObsidian") && BLACKLISTED_BLOCKS.contains(InventoryUtil.getBlock())) return;
                
        if (conditionals.isEnabled("OnlyClick")) {
            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) return;
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        }
        
        int cps = getRandomizedCPS();
        if (stopWatch.hasPassed(1000 / cps)) {
            PlayerUtil.rightClick(true);
            PlayerUtil.setMouseButtonState(1, true);
            stopWatch.reset();
        }

        startDelay.reset();
    };

    public int getRandomizedCPS() {
        int baseCPS = Math.round(cps.getValue());
        int delta = Math.min(Math.round(deltaCPS.getValue()), 20);
        int minCPS = Math.max(1, baseCPS - delta);
        int maxCPS = baseCPS + delta;
        return ThreadLocalRandom.current().nextInt(minCPS, maxCPS + 1);
    }
    
    private void reset() {
        stopWatch.reset();
        InventoryUtil.inventoryStopWatch.reset();
        startDelay.reset();
    }
}