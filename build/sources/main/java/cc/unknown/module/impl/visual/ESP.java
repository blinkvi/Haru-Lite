package cc.unknown.module.impl.visual;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.awt.Color;
import java.util.Arrays;

import cc.unknown.event.render.UpdatePlayerAnglesEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.value.impl.Bool;
import cc.unknown.value.impl.MultiBool;
import cc.unknown.value.impl.Slider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ESP", description = "Renders an in-game ESP (Extra Sensory Perception) overlay.", category = Category.VISUAL)
public class ESP extends Module {    

    public final Slider skeletalWidth = new Slider("Width", this, 0.5f, 0.1f, 5f, 0.1f);
    
	public final MultiBool conditionals = new MultiBool("Conditionals", this, Arrays.asList(
			new Bool("ColorTeams", true),
			new Bool("ShowInvisibles", true),
			new Bool("RedOnDamage", true)));

    @SubscribeEvent
    public void onUpdatePlayerAngles(UpdatePlayerAnglesEvent event) {
    	RenderUtil.updatePlayerAngles(event.player, event.model);
    }
    
	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (PlayerUtil.unusedNames(player)) continue;
            if (player.isInvisible() && !conditionals.isEnabled("ShowInvisibles")) continue;
            if (mc.thePlayer == player) continue;

            float partialTicks = event.partialTicks;
            int color = getPlayerColor(player);
            
            glPushMatrix();
            RenderUtil.setupRenderState(new Color(color), (float) skeletalWidth.getValue());
            RenderUtil.drawSkeleton(player, partialTicks);
            RenderUtil.restoreRenderState();
            glPopMatrix();
        }
    }

    private int getPlayerColor(EntityPlayer player) {
        String name = player.getName();
        if (conditionals.isEnabled("RedOnDamage") && player.hurtTime > 0) return new Color(255, 0, 0).getRGB();
        if (conditionals.isEnabled("ColorTeams")) return ColorUtil.getColorFromTags(player);
        if (FriendUtil.isFriend(name)) return new Color(0, 255, 0).getRGB();
        return getModule(Interface.class).color();
    }
}