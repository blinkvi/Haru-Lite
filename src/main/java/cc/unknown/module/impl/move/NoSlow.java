package cc.unknown.module.impl.move;

import java.util.Arrays;

import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.event.player.SlowDownEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.network.NetworkUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.Mode;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoSlow", description = "Prevents or reduces the slowdown effect caused by certain actions.", category = Category.MOVE)
public class NoSlow extends Module {

    private final Mode mode = new Mode("Mode", this, "Regular", "Regular", "NoItemRelease");
    private final Slider speed = new Slider("Speed", this, 1f, 0.2f, 1f, 0.1f, () -> mode.is("Regular"));

    public final MultiBool whitelist = new MultiBool("ItemsToWhitelist", this, () -> mode.is("Regular"), Arrays.asList(
        new Bool("Sword", false),
        new Bool("Bow", false),
        new Bool("Food", false),
        new Bool("Drink", true)
    ));

    private boolean isWhitelistedItem() {
        return (!whitelist.isEnabled("Sword") && InventoryUtil.isSword()) ||
               (!whitelist.isEnabled("Bow") && InventoryUtil.isBow()) ||
               (!whitelist.isEnabled("Food") && InventoryUtil.isFood()) ||
               (!whitelist.isEnabled("Drink") && InventoryUtil.isDrink());
    }

    @SubscribeEvent
    public void onOutgoing(OutgoingEvent event) {
        if (!isInGame() || !mode.is("NoItemRelease") || !mc.thePlayer.isUsingItem()) return;

        if (InventoryUtil.getItem() instanceof ItemBow) return;

        if (event.packet instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.packet;
            if (packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onSlowDown(SlowDownEvent event) {
        if (!isInGame() || !mode.is("Regular")) return;

        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !isWhitelistedItem()) {
            event.sprint = true;
            float value = speed.getAsFloat();
            event.forwardMultiplier = value;
            event.strafeMultiplier = value;
        }
    }
}
