package cc.unknown.mixin.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.WorldRenderer;

@Mixin(ModelBox.class)
public class MixinModelBox {

	@Shadow
	private TexturedQuad[] quadList;

	@Overwrite
	public void render(WorldRenderer renderer, float scale) {

		boolean b = GL11.glIsEnabled(GL11.GL_CULL_FACE);

		if (b) {
			draw(renderer, scale);
		} else {
			GL11.glEnable(GL11.GL_CULL_FACE);
			draw(renderer, scale);
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
	}

	private void draw(WorldRenderer renderer, float scale) {
		for (int i = 0; i < this.quadList.length; ++i) {
			this.quadList[i].draw(renderer, scale);
		}
	}
}