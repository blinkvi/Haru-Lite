package cc.unknown.module.impl.utility;

import cc.unknown.event.player.OutgoingEvent;
import cc.unknown.event.player.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.InventoryUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoItemRelease", description = "Cancels the item release packet after using an item", category = Category.UTILITY)
public class NoItemRelease extends Module {
		
	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
		Packet<?> packet = event.getPacket();
		
		if (!mc.thePlayer.isUsingItem()) return;
		
		if (packet instanceof C07PacketPlayerDigging) {
			C07PacketPlayerDigging wrapper = (C07PacketPlayerDigging) packet;
			if (wrapper.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {				
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onSlowDown(SlowDownEvent event) {
		if (InventoryUtil.getItem() instanceof ItemBow) return;
		
		if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) {
	        event.setSprint(true);
	        event.setForwardMultiplier(1f);
	        event.setStrafeMultiplier(1f);
	    }
	}
}