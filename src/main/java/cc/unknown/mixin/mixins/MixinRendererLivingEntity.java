package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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

}