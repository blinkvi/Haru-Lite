package cc.unknown.module.impl.utility;

import org.lwjgl.input.Keyboard;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.client.CameraUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "FreeLook", description = "Allows the player to freely move their camera around the viewpoint.", category = Category.UTILITY, key = Keyboard.KEY_LMENU)
public class FreeLook extends Module {

    private boolean freeLookingactivated;

    @Override
    public void onDisable() {
        freeLookingactivated = false;
        CameraUtil.freelooking = false;
        mc.gameSettings.thirdPersonView = 0;
    }

	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
    	if (event.phase == Phase.END) return;
        if (this.getKeyBind() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.getKeyBind())) {
            this.setEnabled(false);
            return;
        }

        if (mc.thePlayer.ticksExisted < 10) {
            stop();
        }
        
        if (Keyboard.isKeyDown(getKeyBind())) {
            if (!freeLookingactivated) {
                freeLookingactivated = true;
                CameraUtil.enable();
                CameraUtil.cameraYaw += 180;
                mc.gameSettings.thirdPersonView = 1;
            }
        } else if (freeLookingactivated) {
            stop();
        }
    }

    private void stop() {
        toggle();
        CameraUtil.freelooking = false;
        freeLookingactivated = false;
        mc.gameSettings.thirdPersonView = 0;
    }
}