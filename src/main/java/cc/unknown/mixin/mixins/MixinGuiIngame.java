package cc.unknown.mixin.mixins;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cc.unknown.Haru;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.impl.visual.Interface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {

	@Shadow
	@Final
	protected Minecraft mc;
	
	@Unique
    public final Pattern LINK_PATTERN = Pattern.compile("(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[A-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)");

	@Unique
	public int scoreBoardHeight = 0;
	
	@Redirect(method = "renderTooltip", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", opcode = Opcodes.GETFIELD))
	private int redirectCurrentItem(InventoryPlayer inventory) {
		return SpoofHandler.getSpoofedSlot();
	}

	@Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
	private void preRenderScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
		Interface inter = Haru.instance.getModuleManager().getModule(Interface.class);

		if (inter.isEnabled() && inter.elements.isEnabled("Scoreboard")) {
			drawScoreboard(scaledRes, objective, objective.getScoreboard(), objective.getScoreboard().getSortedScores(objective));
			ci.cancel();
		}
	}

	@Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
	private void StreamIndicator(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	private void cancelBossHealth(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
	private void cancelPumpkinOverlay(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderDemo", at = @At("HEAD"), cancellable = true)
	private void cancelDemo(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
	private void cancelPortal(CallbackInfo ci) {
		ci.cancel();
	}
	
    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    private void cancelVignette(CallbackInfo ci) {
    	ci.cancel();
    }
	
	@Unique
	public void drawScoreboard(ScaledResolution scaledRes, ScoreObjective objective, Scoreboard scoreboard, Collection<Score> scores) {
		Interface inter = Haru.instance.getModuleManager().getModule(Interface.class);

		if (inter.noRenderScoreboard.get()) return;
		
		List<Score> list = Lists.newArrayList(Iterables.filter(scores, p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));

        Scoreboard scoreboard1 = objective.getScoreboard();
        Collection<Score> collection = scoreboard1.getSortedScores(objective);
        
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        }
        else {
            collection = list;
        }

        int i = mc.fontRendererObj.getStringWidth(objective.getDisplayName());

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard1.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            i = Math.max(i, mc.fontRendererObj.getStringWidth(s));
        }

        int i1 = collection.size() * mc.fontRendererObj.FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i - k1;
        int j = 0;

        if (inter.fixHeight.get()) {
            j1 = Math.max(j1, scoreBoardHeight + i1 + mc.fontRendererObj.FONT_HEIGHT + 17);
        }

        for (Score score1 : collection) {
            ++j;
            ScorePlayerTeam scoreplayerteam1 = scoreboard1.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
            int k = j1 - j * mc.fontRendererObj.FONT_HEIGHT;
            int l = scaledRes.getScaledWidth() - k1 + 2;
            if(inter.hideBackground.get()) {
            	Gui.drawRect(l1 - 2, k, l, k, 1342177280);
            } else {
            	Gui.drawRect(l1 - 2, k, l, k + mc.fontRendererObj.FONT_HEIGHT, 1342177280);
            }
            
            final Matcher linkMatcher = LINK_PATTERN.matcher(s1);
            if(inter.antiStrike.get() && linkMatcher.find()) {
                s1 = "";
            }
            
            mc.fontRendererObj.drawString(s1, l1, k, 553648127);
            
            if (!inter.hideScoreRed.get())
            	mc.fontRendererObj.drawString(s2, l - mc.fontRendererObj.getStringWidth(s2), k, 553648127);

            if (j == collection.size()) {
                String s3 = objective.getDisplayName();
                if(!inter.hideBackground.get()) {
	                Gui.drawRect(l1 - 2, k - mc.fontRendererObj.FONT_HEIGHT - 1, l, k - 1, 1610612736);
	                Gui.drawRect(l1 - 2, k - 1, l, k, 1342177280);
                }
                
                mc.fontRendererObj.drawString(s3, l1 + i / 2 - mc.fontRendererObj.getStringWidth(s3) / 2, k - mc.fontRendererObj.FONT_HEIGHT, 553648127);
            }
        }
    }
}
