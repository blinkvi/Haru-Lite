package cc.unknown.module.impl.utility;

import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "SaveMoveKeys", description = "Automatically retoggles your movekeys after closing the inventory.", category = Category.UTILITY)
public class SaveMoveKeys extends Module {

	private final SliderValue delay = new SliderValue("Delay", this, 0, 0, 1000, 10);
	private boolean lastInGUI = false;
	
	@SubscribeEvent
	public void onPostTick(ClientTickEvent event) {
    	if (event.phase == Phase.START) return;
        if (mc.currentScreen != null) {
            lastInGUI = true;
        } else {
            if (lastInGUI) {
                Haru.instance.threadPool.schedule(() -> {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
                    KeyBinding.onTick(mc.gameSettings.keyBindForward.getKeyCode());
                    KeyBinding.onTick(mc.gameSettings.keyBindBack.getKeyCode());
                    KeyBinding.onTick(mc.gameSettings.keyBindLeft.getKeyCode());
                    KeyBinding.onTick(mc.gameSettings.keyBindRight.getKeyCode());
                    KeyBinding.onTick(mc.gameSettings.keyBindSprint.getKeyCode());
                    KeyBinding.onTick(mc.gameSettings.keyBindSneak.getKeyCode());
                    KeyBinding.onTick(mc.gameSettings.keyBindJump.getKeyCode());
                }, (long) delay.getValue(), TimeUnit.MILLISECONDS);
            }

            lastInGUI = false;
        }
	}
}