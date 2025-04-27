package cc.unknown.module.impl.move;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.event.player.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoSlow", description = "Prevents or reduces the slowdown effect caused by certain actions.", category = Category.MOVE)
public class NoSlow extends Module {
	private final SliderValue speed = new SliderValue("Speed", this, 1f, 0.2f, 1f, 0.1f);
	private final BoolValue easterEgg = new BoolValue("Intrusive", this, false);

	@SubscribeEvent
	public void onSlowDown(SlowDownEvent event) {
	    if (!isInGame()) return;
	    if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) {
	        event.sprint = true;
	        event.forwardMultiplier = speed.getValue();
	        event.strafeMultiplier = speed.getValue();
	    }
	}
	
    @SubscribeEvent
    public void onPreAttack(PrePositionEvent event) {
	    if (!isInGame()) return;
        if (mc.thePlayer.getItemInUseDuration() == 1 && easterEgg.get() && NetworkUtil.isServer("universocraft.com")) {
	        PacketUtil.sendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 9));
	        PacketUtil.sendNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
	};
}