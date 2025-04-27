package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.PostRenderLivingEvent;
import cc.unknown.event.impl.PreRenderLivingEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin({ RendererLivingEntity.class })
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends MixinRender<T> {
	@Shadow
	protected ModelBase mainModel;

	@Shadow
	protected boolean renderOutlines;

	@Shadow
	protected abstract float getSwingProgress(T paramT, float paramFloat);

	@Shadow
	protected abstract float interpolateRotation(float paramFloat1, float paramFloat2, float paramFloat3);

	@Shadow
	protected abstract void renderLivingAt(T paramT, double paramDouble1, double paramDouble2, double paramDouble3);

	@Shadow
	protected abstract float handleRotationFloat(T paramT, float paramFloat);

	@Shadow
	protected abstract void rotateCorpse(T paramT, float paramFloat1, float paramFloat2, float paramFloat3);

	@Shadow
	protected abstract void preRenderCallback(T paramT, float paramFloat);

	@Shadow
	protected abstract boolean setScoreTeamColor(T paramT);

	@Shadow
	protected abstract void renderModel(T paramT, float paramFloat1, float paramFloat2, float paramFloat3,
			float paramFloat4, float paramFloat5, float paramFloat6);

	@Shadow
	protected abstract void unsetScoreTeamColor();

	@Shadow
	protected abstract boolean setDoRenderBrightness(T paramT, float paramFloat);

	@Shadow
	protected abstract void unsetBrightness();

	@Shadow
	protected abstract void renderLayers(T paramT, float paramFloat1, float paramFloat2, float paramFloat3,
			float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7);
	
	@Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
	private void onDoRenderPre(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
	    PreRenderLivingEvent event = new PreRenderLivingEvent(entity, x, y, z);
	    Haru.eventBus.handle(event);
	    if (event.isCanceled()) {
	        ci.cancel();
	    }
	}

	@Inject(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;doRender(Lnet/minecraft/entity/Entity;DDDFF)V", shift = At.Shift.AFTER), cancellable = true)
	private void onDoRenderPost(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
	    PostRenderLivingEvent event = new PostRenderLivingEvent(entity, x, y, z);
	    Haru.eventBus.handle(event);
	    if (event.isCanceled()) {
	        ci.cancel();
	    }
	}
}