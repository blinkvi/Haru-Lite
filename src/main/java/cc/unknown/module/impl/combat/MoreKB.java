package cc.unknown.module.impl.combat;

import cc.unknown.event.player.AttackEvent;
import cc.unknown.event.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.network.PacketUtil;
import cc.unknown.util.client.system.Clock;
import cc.unknown.value.impl.Mode;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "MoreKB", description = "Amplifies knockback effect on opponents during combat.", category = Category.COMBAT)
public class MoreKB extends Module {

    private final Mode mode = new Mode("Mode", this, "Wtap", "Wtap", "Stap", "ShiftTap", "Packet", "Legit");
    private final Clock wtapTimer = new Clock();
    private int ticks;

    @SubscribeEvent
    public void onAttack(AttackEvent event) {
        if (wtapTimer.reached(500L)) {
            wtapTimer.reset();
            ticks = 2;
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        switch (ticks) {
            case 2:
                switch (mode.getMode()) {
                    case "Wtap":
                        ReflectUtil.setPressed(mc.gameSettings.keyBindForward, false);
                        break;
                    case "Stap":
                    	ReflectUtil.setPressed(mc.gameSettings.keyBindForward, false);
                    	ReflectUtil.setPressed(mc.gameSettings.keyBindBack, true);
                        break;
                    case "ShiftTap":
                        ReflectUtil.setPressed(mc.gameSettings.keyBindSneak, true);
                        break;
                    case "Packet":
                        PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        break;
                    case "Legit":
                        mc.thePlayer.setSprinting(false);
                        break;
                }
                ticks--;
                break;
            case 1:
            	switch (mode.getMode()) {
                    case "Wtap":
                    	ReflectUtil.setPressed(mc.gameSettings.keyBindForward, GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
                        break;
                    case "Stap":
                    	ReflectUtil.setPressed(mc.gameSettings.keyBindForward, GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
                    	ReflectUtil.setPressed(mc.gameSettings.keyBindBack, GameSettings.isKeyDown(mc.gameSettings.keyBindBack));
                        break;
                    case "ShiftTap":
                    	ReflectUtil.setPressed(mc.gameSettings.keyBindSneak, GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));
                        break;
                    case "Packet":
                        PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                        break;
                    case "Legit":
                        mc.thePlayer.setSprinting(true);
                        break;
                }
                ticks--;
                break;
        }
    }
}
