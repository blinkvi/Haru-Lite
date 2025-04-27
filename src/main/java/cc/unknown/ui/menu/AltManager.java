package cc.unknown.ui.menu;

import static cc.unknown.util.render.client.ColorUtil.gray;
import static cc.unknown.util.render.client.ColorUtil.green;
import static cc.unknown.util.render.client.ColorUtil.red;
import static cc.unknown.util.render.client.ColorUtil.yellow;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import cc.unknown.Haru;
import cc.unknown.module.impl.visual.Interface;
import cc.unknown.ui.menu.impl.TextField;
import cc.unknown.util.alt.AltGen;
import cc.unknown.util.alt.MicrosoftAccount;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.structure.vectors.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Session;

public class AltManager extends GuiScreen {
    private static TextField usernameBox;
    public static String status = yellow + "Idle...";
    
    @Override
    public void initGui() {
    	this.buttonList.clear();
        int boxHeight = 24;
        int padding = 4;
        int button1X = this.width / 2 - 90 / 2;
        int buttonSpacing = 2;
        
        Vector2d position = new Vector2d(this.width / 2 - 130 / 2, this.height / 2 - 24);
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) 130, (int) boxHeight);

        this.buttonList.add(new GuiButton(1, button1X - 10, (int) (position.y + boxHeight + padding), 50, boxHeight, "Random"));

        this.buttonList.add(new GuiButton(2, button1X + 60 + buttonSpacing - 12, (int) (position.y + (boxHeight + padding)), 50, boxHeight, "Browser"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        drawGradient(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, Haru.modMngr.getModule(Interface.class).color());
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        usernameBox.drawTextBox();
        
        GlStateManager.pushMatrix();

        int backgroundHeight = (int) (FontUtil.getFontRenderer("comfortaa.ttf", 16).getHeight() + 5);
        int backgroundY = (int) (height / 2 - 35 - (backgroundHeight / 2));

        FontUtil.getFontRenderer("comfortaa.ttf", 16).drawCenteredString(
            status,
            width / 2, 
            (int) (backgroundY + backgroundHeight / 2 - FontUtil.getFontRenderer("consolas.ttf", 16).getHeight() / 2), 
            Color.WHITE.getRGB()
        );

        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));

        GlStateManager.popMatrix();
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	usernameBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	usernameBox.textboxKeyTyped(typedChar, keyCode);
        String inputText = usernameBox.getText().trim();
        if (inputText.length() > 16) {
            usernameBox.setText(inputText.substring(0, 16));
        }
    	
        if (typedChar == '\r') {            
            if (AltGen.validate(inputText)) {
                usernameBox.setText(inputText);
                status = gray + "Logeado como > " + green + inputText;
                ReflectUtil.setSession(new Session(inputText, "none", "none", "mojang"));
            } else {
                status = red + "El nombre ingresado no es válido.";
            }
        }
        
        if (keyCode == 1) {            
        	mc.displayGuiScreen(new GuiMultiplayer((GuiScreen) (Object) this));
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
    	switch (button.id) {
        case 1:        	
        	String name = AltGen.generate();
        	if (name != null && AltGen.validate(name)) {
        		usernameBox.setText(name);
        	}
        	status = gray + "Logeado como > " + green + name;
        	ReflectUtil.setSession(new Session(name, "none", "none", "mojang"));
        	break;
        case 2:
        	status = gray + "Abriendo navegador...";
            MicrosoftAccount.create();
        	break;
        }
    }
    
    @Override
    public void onGuiClosed() {
        status = yellow + "Idle...";
        usernameBox.setText("");
    }
    
    protected void drawGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();

        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        worldrenderer.pos((double)right, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
