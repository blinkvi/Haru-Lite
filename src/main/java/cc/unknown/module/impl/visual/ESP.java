package cc.unknown.module.impl.visual;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.awt.Color;

import cc.unknown.event.render.UpdatePlayerAnglesEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.value.impl.Bool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ESP", description = "Renders an in-game ESP (Extra Sensory Perception) overlay.", category = Category.VISUAL)
public class ESP extends Module {
	
	private final Bool colorTeams = new Bool("ColorTeams", this, true);
	private final Bool showInvis = new Bool("ShowInvisibles", this, true);
	private final Bool redOnDamage = new Bool("RedOnDamage", this, true);

    @SubscribeEvent
    public void onUpdatePlayerAngles(UpdatePlayerAnglesEvent event) {
    	RenderUtil.updatePlayerAngles(event.player, event.model);
    }
    
	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (PlayerUtil.unusedNames(player)) continue;
            if (player.isInvisible() && !showInvis.get()) continue;
            if (mc.thePlayer == player) continue;

            float partialTicks = event.partialTicks;
            int color = getPlayerColor(player);
            
            glPushMatrix();
            RenderUtil.setupRenderState(new Color(color));
            RenderUtil.drawSkeleton(player, partialTicks);
            RenderUtil.restoreRenderState();
            glPopMatrix();
        }
    }

    private int getPlayerColor(EntityPlayer player) {
        String name = player.getName();
        if (redOnDamage.get() && player.hurtTime > 0) return new Color(255, 0, 0).getRGB();
        if (colorTeams.get()) return ColorUtil.getColorFromTags(player);
        if (FriendUtil.isFriend(name)) return new Color(0, 255, 0).getRGB();
        return getModule(Interface.class).color();
    }
}