package cc.unknown.module.impl.utility;

import java.util.Arrays;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "FastMine", description = "Increases the speed at which blocks can be broken.", category = Category.UTILITY)
public class FastMine extends Module {
	
	private final Slider ticks = new Slider("Ticks", this, 1, 1, 50);

	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("BlockHitDelay", true),
			new Bool("Instant", false),
			new Bool("IgnoreUnbreakable", false),
			new Bool("IgnoreBeds", false),
			new Bool("OnlyTools", false)
	));

	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
    	if (event.phase == Phase.END) return;
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
	}
	
	@SubscribeEvent
	public void onPreAttack(PrePositionEvent event) {
		if (conditionals.isEnabled("BlockHitDelay")) {
			ReflectUtil.setBlockHitDelay(0);
		}
	}

	private boolean isHoldingTool() {
		ItemStack heldItem = mc.thePlayer.getHeldItem();
		return heldItem != null && (heldItem.getItem() instanceof ItemTool);
	}
	
	private boolean applyConditions() {
		if (!isInGame()) return false;
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
