package cc.unknown.module.impl.visual;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.awt.Color;
import java.util.Arrays;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.RenderWorldLastEvent;
import cc.unknown.event.impl.UpdatePlayerAnglesEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.value.impl.BoolValue;
import cc.unknown.value.impl.MultiBoolValue;
import cc.unknown.value.impl.SliderValue;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "ESP", description = "Renders an in-game ESP (Extra Sensory Perception) overlay.", category = Category.VISUAL)
public class ESP extends Module {    

    public final SliderValue skeletalWidth = new SliderValue("Width", this, 0.5f, 0.1f, 5f, 0.1f);
    
	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("ColorTeams", true),
			new BoolValue("ShowInvisibles", true),
			new BoolValue("RedOnDamage", true)));

    @EventLink
    public final Listener<UpdatePlayerAnglesEvent> onUpdatePlayerAngles = event -> RenderUtil.updatePlayerAngles(event.entityPlayer, event.modelBiped);
    
    @EventLink
    public final Listener<RenderWorldLastEvent> onRender3D = event -> {
		if (!PlayerUtil.isInGame()) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (PlayerUtil.unusedNames(player)) continue;
            if (player.isInvisible() && !conditionals.isEnabled("ShowInvisibles")) continue;
            if (mc.thePlayer == player) continue;

            float partialTicks = event.partialTicks;
            int color = getPlayerColor(player);
            
            glPushMatrix();
            RenderUtil.setupRenderState(new Color(color), skeletalWidth.getValue());
            RenderUtil.drawSkeleton(player, partialTicks);
            RenderUtil.restoreRenderState();
            glPopMatrix();
        }
    };

    private int getPlayerColor(EntityPlayer player) {
        String name = player.getName();
        if (conditionals.isEnabled("RedOnDamage") && player.hurtTime > 0) return new Color(255, 0, 0).getRGB();
        if (conditionals.isEnabled("ColorTeams")) return ColorUtil.getColorFromTags(player);
        if (FriendUtil.isFriend(name)) return new Color(0, 255, 0).getRGB();
        return getModule(Interface.class).color();
    }
}