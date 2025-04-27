package cc.unknown.module.impl.move;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PrePositionEvent;
import cc.unknown.event.impl.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@ModuleInfo(name = "NoSlow", description = "Prevents or reduces the slowdown effect caused by certain actions.", category = Category.MOVE)
public class NoSlow extends Module {
	private final SliderValue speed = new SliderValue("Speed", this, 1f, 0.2f, 1f, 0.1f);
	private final BoolValue easterEgg = new BoolValue("Intrusive", this, false);
	
    @EventLink
    public final Listener<SlowDownEvent> onSlowDown = event -> {
	    if (!PlayerUtil.isInGame()) return;
	    if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) {
	        event.sprint = true;
	        event.forwardMultiplier = speed.getValue();
	        event.strafeMultiplier = speed.getValue();
	    }
    };

    @EventLink
    public final Listener<PrePositionEvent> onPrePosition = event -> {
	    if (!PlayerUtil.isInGame()) return;
        if (mc.thePlayer.getItemInUseDuration() == 1 && easterEgg.get() && NetworkUtil.isServer("universocraft.com")) {
	        PacketUtil.sendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 9));
	        PacketUtil.sendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    };
}