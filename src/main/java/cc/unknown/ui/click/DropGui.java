package cc.unknown.ui.click;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.visual.ClickGUI;
import cc.unknown.util.Accessor;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.structure.list.SList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class DropGui extends GuiScreen {
    private final SList<PanelRenderer> windows = new SList<>();
    private int guiYMoveLeft = 0;
    private static final int SCROLL_SPEED = 30;
    private int lastWidth = -1;
    private int lastHeight = -1;

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution sr = new ScaledResolution(Accessor.mc);
        float screenWidth = sr.getScaledWidth();
        float screenHeight = sr.getScaledHeight();

        if (windows.isEmpty()) {
            float buttonWidth = 100;
            float spacingY = 30;
            float startX = (screenWidth - buttonWidth) / 2.0f;
            float startY = screenHeight / 4.0f;

            final int[] index = {0};

            Arrays.stream(Category.values()).forEach(category -> {
                float x = startX;
                float y = startY + index[0] * (buttonWidth / 2 - spacingY);
                PanelRenderer panel = new PanelRenderer(category, x, y);
                panel.setRelativeX(x / screenWidth);
                panel.setRelativeY(y / screenHeight);
                windows.add(panel);
                index[0]++;
            });
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Accessor.mc);
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        if (screenWidth != lastWidth || screenHeight != lastHeight) {
            lastWidth = screenWidth;
            lastHeight = screenHeight;

            windows.forEach(panel -> {
                panel.setX(panel.getRelativeX() * screenWidth);
                panel.setY(panel.getRelativeY() * screenHeight);
            });
        }

        ClickGUI gui = Haru.instance.getModuleManager().getModule(ClickGUI.class);
        int alpha = Math.max(0, Math.min(255, (int) gui.alpha.getValue()));
        RenderUtil.drawRect(0, 0, screenWidth, screenHeight, new Color(0, 0, 0, alpha).getRGB());

        if (guiYMoveLeft != 0) {
            int step = (int) (guiYMoveLeft * 0.15);
            if (step == 0) {
                guiYMoveLeft = 0;
            } else {
                windows.forEach(window -> window.y += step);
                guiYMoveLeft -= step;
            }
        }

        windows.forEach(window -> window.drawScreen(mouseX, mouseY));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(window -> window.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        windows.forEach(window -> window.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) mouseScrolled(dWheel);
    }

    private void mouseScrolled(int dWheel) {
        guiYMoveLeft += (dWheel > 0 ? SCROLL_SPEED : -SCROLL_SPEED);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_UP: guiYMoveLeft += SCROLL_SPEED;
            case Keyboard.KEY_DOWN: guiYMoveLeft -= SCROLL_SPEED;
            case Keyboard.KEY_ESCAPE: mc.displayGuiScreen(null);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public SList<PanelRenderer> getWindows() {
        return windows;
    }
}