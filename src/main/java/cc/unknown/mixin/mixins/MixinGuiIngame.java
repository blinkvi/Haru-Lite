package cc.unknown.mixin.mixins;

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

import cc.unknown.Haru;
import cc.unknown.handlers.SpoofHandler;
import cc.unknown.module.impl.visual.AntiDebuff;
import cc.unknown.module.impl.visual.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.potion.Potion;

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
	
    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
    private boolean redirectRenderGameOverlay(GuiIngame gui, Potion potion) {
    	AntiDebuff antiDebuff = Haru.instance.getModuleManager().getModule(AntiDebuff.class);
        if (antiDebuff != null && potion == Potion.confusion) {
            return false;
        }
        return Minecraft.getMinecraft().thePlayer.isPotionActive(potion);
    }

	@Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
	private void StreamIndicator(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	private void cancelBossHealth(CallbackInfo ci) {
		NoRender no = Haru.instance.getModuleManager().getModule(NoRender.class);
		if (no.boss.get())
			ci.cancel();
	}

	@Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
	private void cancelPumpkinOverlay(CallbackInfo ci) {
		NoRender no = Haru.instance.getModuleManager().getModule(NoRender.class);
		if (no.pumpkin.get())
			ci.cancel();
	}

	@Inject(method = "renderDemo", at = @At("HEAD"), cancellable = true)
	private void cancelDemo(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
	private void cancelPortal(CallbackInfo ci) {
		NoRender no = Haru.instance.getModuleManager().getModule(NoRender.class);
		if (no.portal.get())
			ci.cancel();
	}
	
    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    private void cancelVignette(CallbackInfo ci) {
		NoRender no = Haru.instance.getModuleManager().getModule(NoRender.class);
		if (no.vignette.get())
			ci.cancel();
    }
}
