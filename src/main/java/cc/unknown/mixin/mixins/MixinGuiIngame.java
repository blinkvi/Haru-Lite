package cc.unknown.mixin.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.impl.visual.Interface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.scoreboard.ScoreObjective;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {

	@Shadow
	@Final
	protected Minecraft mc;

	@Redirect(method = "renderTooltip", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", opcode = Opcodes.GETFIELD))
	private int redirectCurrentItem(InventoryPlayer inventory) {
		return SpoofHandler.getSpoofedSlot();
	}

	@Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
	private void preRenderScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
		Interface inter = Haru.instance.getModuleManager().getModule(Interface.class);

		if (inter.isEnabled() && inter.elements.isEnabled("Scoreboard")) {
			inter.drawScoreboard(scaledRes, objective, objective.getScoreboard(), objective.getScoreboard().getSortedScores(objective));
			ci.cancel();
		}
	}

	@Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
	private void StreamIndicator(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	private void bossHealth(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
	private void pumpkinOverlay(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderDemo", at = @At("HEAD"), cancellable = true)
	private void renderDemo(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
	private void renderPortal(CallbackInfo ci) {
		ci.cancel();
	}
}
