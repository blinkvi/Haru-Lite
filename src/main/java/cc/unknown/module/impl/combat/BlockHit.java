package cc.unknown.module.impl.combat;

import cc.unknown.event.netty.OutgoingEvent;
import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.value.impl.Bool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BlockHit", description = "Automatically blocks for you.", category = Category.COMBAT)
public class BlockHit extends Module {

    private final Bool swingCheck = new Bool("Swing Check", this, true);

    private final Clock timer = new Clock();
    private EntityPlayer target;
    private boolean predicted;

    @Override
    public void onEnable() {
        timer.reset();
        predicted = false;
        target = null;
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        double closestDistance = 16.0;
        EntityPlayer closestTarget = null;

        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity == mc.thePlayer || PlayerUtil.isTeam(entity)) continue;

            double dist = mc.thePlayer.getDistanceToEntity(entity);
            if (dist <= closestDistance) {
                closestDistance = dist;
                closestTarget = entity;
            }
        }

        target = closestTarget;
    }

    @SubscribeEvent
    public void onOutgoing(OutgoingEvent event) {
        if (!isInGame() || mc.thePlayer == null || mc.theWorld == null) return;

        Packet<?> packet = event.packet;

        boolean shouldBlock =
                InventoryUtil.isSword()
                && !(packet instanceof C02PacketUseEntity)
                && target != null
                && target.swingProgressInt > 0
                && (mc.thePlayer.hurtTime == 0 && timer.hasPassed(500) || mc.thePlayer.hurtTime == 9)
                && (!swingCheck.get() || mc.thePlayer.isSwingInProgress);

        if (shouldBlock) {
            ReflectUtil.setPressed(mc.gameSettings.keyBindUseItem, true);
            predicted = true;
            timer.reset();
        }

        if (predicted && (mc.thePlayer.isBlocking() || !InventoryUtil.isSword())) {
            ReflectUtil.setPressed(mc.gameSettings.keyBindUseItem, false);
            predicted = false;
        }
    }
}