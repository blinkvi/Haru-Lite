package cc.unknown.mixin.mixins;

import java.awt.Color;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.render.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

@Mixin(GuiButton.class)
public abstract class MixinGuiButton {
	
	@Shadow
    public int width;

	@Shadow
	public int height;

	@Shadow
	public int xPosition;

	@Shadow
	public int yPosition;
	
	@Shadow
	public String displayString;
	
	@Shadow
	protected boolean hovered;
	
	@Shadow
	public boolean visible;

	@Overwrite
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
        	hovered = mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height;
			int color = hovered ? new Color(255, 255, 255).getRGB() : new Color(200, 200, 200).getRGB();
			
			RenderUtil.drawRoundedRect((double)xPosition, (double)yPosition, (double)width, (double)height, (float)20, new Color(1, 1, 1, 150).getRGB());
	
			int textWidth = (int) FontUtil.getFontRenderer("comfortaa.ttf", 16).getStringWidth(displayString);
			int textHeight = (int) FontUtil.getFontRenderer("comfortaa.ttf", 16).getHeight();
			float centeredX = xPosition + (width / 2.0f) - (textWidth / 2.0f);
			float centeredY = yPosition + (height / 1.6f) - (textHeight / 1.6f);
			
			this.mouseDragged(mc, mouseX, mouseY);
			
			FontUtil.getFontRenderer("comfortaa.ttf", 16).drawStringWithShadow(displayString, centeredX, centeredY, color);
        }
	}
	
	@Overwrite
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) { }
}
