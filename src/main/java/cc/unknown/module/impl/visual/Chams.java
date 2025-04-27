package cc.unknown.module.impl.visual;

import org.lwjgl.opengl.GL11;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PostRenderLivingEvent;
import cc.unknown.event.impl.PreRenderLivingEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "Chams", description = "Render entities through walls.", category = Category.VISUAL)
public class Chams extends Module {

	@EventLink
	public final Listener<PostRenderLivingEvent> onPostRenderLiving = event -> render(2);
	
	@EventLink
	public final Listener<PreRenderLivingEvent> onPreRenderLiving = event -> render(1);
	
    private void render(int pre) {
		if (!PlayerUtil.isInGame()) return;

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