package cc.unknown.module.impl.utility;

import java.util.Arrays;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PrePositionEvent;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(name = "FastBreak", description = "Increases the speed at which blocks can be broken.", category = Category.UTILITY)
public class FastBreak extends Module {
	
	private final SliderValue ticks = new SliderValue("Ticks", this, 1, 1, 10);

	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("BlockHitDelay", true),
			new BoolValue("Instant", false),
			new BoolValue("IgnoreUnbreakable", false),
			new BoolValue("IgnoreBeds", false),
			new BoolValue("OnlyTools", false)
	));

    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
		if (!applyConditions()) return;
		
		if (conditionals.isEnabled("Instant")) {
			ReflectUtil.setCurBlockDamage(1f);
		}
		
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
		    BlockPos blockPos = mc.objectMouseOver.getBlockPos();
		    Block block = mc.theWorld.getBlockState(blockPos).getBlock();

		    float blockHardness = block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos);
		    if (blockHardness > 0) {
		        double faster = blockHardness * ticks.getValue();

		        float curDamage = ReflectUtil.getCurBlockDamage();
		        if (curDamage >= 1.0f - faster && curDamage < 0.99f) {
		            ReflectUtil.setCurBlockDamage(0.99f);
		        }
		    }
		}
	};
	
    @EventLink
    public final Listener<PrePositionEvent> onPrePosition = event -> {
		if (conditionals.isEnabled("BlockHitDelay")) {
			ReflectUtil.setBlockHitDelay(0);
		}
    };

	private boolean isHoldingTool() {
		ItemStack heldItem = mc.thePlayer.getHeldItem();
		return heldItem != null && (heldItem.getItem() instanceof ItemTool);
	}
	
	private boolean applyConditions() {
		if (!PlayerUtil.isInGame()) return false;
		if (mc.objectMouseOver == null) return false;
		BlockPos blockPos = mc.objectMouseOver.getBlockPos();
		if (blockPos == null) return false;
		Block block = mc.theWorld.getBlockState(blockPos).getBlock();
		if (conditionals.isEnabled("IgnoreUnbreakable") && block.getBlockHardness(mc.theWorld, blockPos) == -1) return false;
		if (conditionals.isEnabled("IgnoreBeds") && block instanceof BlockBed) return false;
		if (conditionals.isEnabled("OnlyTools") && !isHoldingTool()) return false;
		return true;
	}
}
