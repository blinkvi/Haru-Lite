package cc.unknown.handlers;
import cc.unknown.event.player.PreAttackEvent;
import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.impl.move.NoSlow;
import cc.unknown.module.impl.utility.NoItemRelease;
import cc.unknown.ui.click.Window;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.player.InventoryUtil;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SettingsHandler implements Accessor {
	
    @SubscribeEvent
    public void onPreAttack(PreAttackEvent event) {
		if (!isInGame()) return;
		
		for (Window window : getDropGui().getWindows()) {
			if (window.getSettingBools().get(0).get()) {
				ReflectUtil.setLeftClickCounter(0);		
			}
			
			if (window.getSettingBools().get(1).get()) {
				ReflectUtil.setJumpTicks(0);		
			}
		}
    }
    
	@SubscribeEvent
	public void onPrePosition(PrePositionEvent event) {
		if (!isInGame()) return;
		for (Window window : getDropGui().getWindows()) {
			if (window.getSettingBools().get(2).get() && mc.thePlayer.isUsingItem() && InventoryUtil.getItemStack().getItem() instanceof ItemFood) {
				if (getModule(NoSlow.class).isEnabled() && getModule(NoItemRelease.class).isEnabled()) return;
				
	            int foodUseDuration = mc.thePlayer.getItemInUseDuration();
	            int halfDuration = (int) 26;
	            if (foodUseDuration >= halfDuration) {
	            	mc.thePlayer.stopUsingItem();
	            }
			}
		}

	}
}
