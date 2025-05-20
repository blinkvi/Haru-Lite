package cc.unknown.module.impl.move;
import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.event.player.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.Slider;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoSlow", description = "Prevents or reduces the slowdown effect caused by certain actions.", category = Category.MOVE)
public class NoSlow extends Module {
	
	private final Mode mode = new Mode("Mode", this, "Regular", "Regular", "NoItemRelease");
	private final Slider speed = new Slider("Speed", this, 1f, 0.2f, 1f, 0.1f, () -> mode.is("Regular"));
	private final Bool easterEgg = new Bool("Intrusive", this, false, () -> mode.is("Regular") && NetworkUtil.isServer("universocraft.com"));
	
	@Override
	public void onEnable() {
		if (!isInGame()) return;
	}
	
	@SubscribeEvent
	public void onOutgoing(OutgoingEvent event) {
		Packet<?> packet = event.packet;
		
		if (!mc.thePlayer.isUsingItem()) return;
		if (InventoryUtil.getItem() instanceof ItemBow) return;
		if (!mode.is("NoItemRelease")) return;
		
		if (packet instanceof C07PacketPlayerDigging) {
			C07PacketPlayerDigging wrapper = (C07PacketPlayerDigging) packet;
			if (wrapper.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {				
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onSlowDown(SlowDownEvent event) {
	    if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) {
	        event.sprint = true;
	        event.forwardMultiplier = (float) (mode.is("Regular") ? speed.getValue() : 1.0f);
	        event.strafeMultiplier = (float) (mode.is("Regular") ? speed.getValue() : 1.0f);
	    }
	}
	
    @SubscribeEvent
    public void onPreAttack(PrePositionEvent event) {
    	if (!mode.is("Regular")) return;
    	
        if (mc.thePlayer.getItemInUseDuration() == 1 && easterEgg.get() && NetworkUtil.isServer("universocraft.com")) {
	        PacketUtil.sendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 9));
	        PacketUtil.sendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
	};
}