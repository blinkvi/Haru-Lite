package cc.unknown.module.impl.utility;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.client.CameraUtil;

@ModuleInfo(name = "FreeLook", description = "Allows the player to freely move their camera around the viewpoint.", category = Category.UTILITY, key = Keyboard.KEY_LMENU)
public class FreeLook extends Module {

    private boolean bool;

    @Override
    public void onDisable() {
        bool = false;
        CameraUtil.freelooking = false;
        mc.gameSettings.thirdPersonView = 0;
    }

    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
		if (!PlayerUtil.isInGame()) return;

        if (this.getKeyBind() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.getKeyBind())) {
            this.setEnabled(false);
            return;
        }

        if (mc.thePlayer.ticksExisted < 10) {
            stop();
        }
        
        if (Keyboard.isKeyDown(getKeyBind())) {
            if (!bool) {
                bool = true;
                CameraUtil.enable();
                CameraUtil.cameraYaw += 180;
                mc.gameSettings.thirdPersonView = 1;
            }
        } else if (bool) {
            stop();
        }
    };

    private void stop() {
        toggle();
        CameraUtil.freelooking = false;
        bool = false;
        mc.gameSettings.thirdPersonView = 0;
    }
}