package cc.unknown.module.impl.visual;

import org.lwjgl.opengl.GL11;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("rawtypes")
@ModuleInfo(name = "Chams", description = "Render entities through walls.", category = Category.VISUAL)
public class Chams extends Module {
	
	@SubscribeEvent
	public void onPreRenderLiving(RenderLivingEvent.Pre event) {
		render(1);
	}
    
	@SubscribeEvent
	public void onPostRenderLiving(RenderLivingEvent.Post event) {
		render(2);
	}
	
    private void render(int pre) {
		for (EntityPlayer player : mc.theWorld.playerEntities) {
			if (player == mc.thePlayer || player.isDead || player == null) {
				continue;
			}
			
			switch (pre) {
			case 1:
	            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
	            GL11.glPolygonOffset(1.0F, -1000000F);
	            break;
			case 2:
	            GL11.glPolygonOffset(1.0F, 1000000F);
	            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			}
		}
    }
}