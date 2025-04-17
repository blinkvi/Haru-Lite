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
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ESP", description = "Renders an in-game ESP (Extra Sensory Perception) overlay.", category = Category.VISUAL)
public class ESP extends Module {    

	private final BoolValue colorTeams = new BoolValue("ColorTeams", this, false);
	private final BoolValue checkInvis = new BoolValue("ShowInvisibles", this, false);
	private final BoolValue redDamage = new BoolValue("RedOnDamage", this, false);
	
    public final SliderValue skeletalWidth = new SliderValue("Width", this, 0.5f, 0.1f, 5f, 0.1f);

    @SubscribeEvent
    public void onUpdatePlayerAngles(UpdatePlayerAnglesEvent event) {
    	RenderUtil.updatePlayerAngles(event.entityPlayer, event.modelBiped);
    }
    
	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (PlayerUtil.unusedNames(player) || mc.gameSettings.thirdPersonView == 0) continue;
            if (player.deathTime > 0 || (player.isInvisible() && !checkInvis.get())) continue;

            float partialTicks = event.partialTicks;
            int color = getPlayerColor(player);
            
            glPushMatrix();
            RenderUtil.setupRenderState(new Color(color), skeletalWidth.getValue());
            RenderUtil.drawSkeleton(player, partialTicks);
            RenderUtil.restoreRenderState();
            glPopMatrix();
        }
    }

    private int getPlayerColor(EntityPlayer player) {
        String name = player.getName();
        if (redDamage.get() && player.hurtTime > 0) return new Color(255, 0, 0).getRGB();
        if (colorTeams.get()) return ColorUtil.getColorFromTags(player);
        if (FriendUtil.isFriend(name)) return new Color(0, 255, 0).getRGB();
        return getModule(Interface.class).color(0);
    }
}