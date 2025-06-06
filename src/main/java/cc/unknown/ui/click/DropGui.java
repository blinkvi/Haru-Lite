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
import cc.unknown.util.render.enums.StickersType;
import cc.unknown.util.structure.list.SList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class DropGui extends GuiScreen {
    private final SList<PanelRenderer> windows = new SList<>();
    private int guiYMoveLeft = 0;
    private static final int SCROLL_SPEED = 30;
    public float startX, startY;
    public float x, y;
    public float buttonWidth, spacingY;
    private PanelRenderer win;

    public DropGui() {
        ScaledResolution sr = new ScaledResolution(Accessor.mc);
        float screenWidth = sr.getScaledWidth();
        float screenHeight = sr.getScaledHeight();

        buttonWidth = 100;
        spacingY = 30;

        startX = (screenWidth - buttonWidth) / 2.0f;
        startY = screenHeight / 4.0f;

        final int[] index = {0};

        Arrays.stream(Category.values()).forEach(category -> {
            x = startX;
            y = startY + index[0] * (buttonWidth / 2 - spacingY);
            windows.add(new PanelRenderer(category, x, y));
            index[0]++;
        });
    }

    @Override
    public void initGui() {
        super.initGui();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	ScaledResolution sr = new ScaledResolution(Accessor.mc);	
    	ClickGUI gui = Haru.instance.getModuleManager().getModule(ClickGUI.class);
        StickersType sticker = gui.waifuType.getMode(StickersType.class);
        
        int alpha = Math.max(0, Math.min(255, (int) gui.alpha.getValue()));
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, alpha));
	
        if (guiYMoveLeft != 0) {
            int step = (int) (guiYMoveLeft * 0.15);
            if (step == 0) {
                guiYMoveLeft = 0;
            } else {
                for (PanelRenderer window : windows) {
                    window.y = window.y + step;
                }
                guiYMoveLeft -= step;
            }
        }
        
        if (sticker != StickersType.NONE) {
        	RenderUtil.image(new ResourceLocation(
        			sticker.imagePath),
        			(int) 2 / sr.getScaledWidth() + (float) gui.width.getValue(), 
        			(int) 2 / sr.getScaledHeight() + (float) gui.height.getValue(), (int) sticker.width, (int) sticker.height);
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
        if (dWheel != 0) {
            mouseScrolled(dWheel);
        }
    }

    public void mouseScrolled(int dWheel) {
        if (dWheel > 0) {
            guiYMoveLeft += SCROLL_SPEED;
        } else if (dWheel < 0) {
            guiYMoveLeft -= SCROLL_SPEED;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_UP:
                guiYMoveLeft += SCROLL_SPEED;
                break;
            case Keyboard.KEY_DOWN:
                guiYMoveLeft -= SCROLL_SPEED;
                break;
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(null);
                break;
            default:
                break;
        }

        super.keyTyped(typedChar, keyCode);
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
    	Haru.instance.getCfgManager().saveFiles();
        super.onGuiClosed();
    }

	public SList<PanelRenderer> getWindows() {
		return windows;
	}

	public int getGuiYMoveLeft() {
		return guiYMoveLeft;
	}

	public static int getScrollSpeed() {
		return SCROLL_SPEED;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getButtonWidth() {
		return buttonWidth;
	}

	public float getSpacingY() {
		return spacingY;
	}

	public PanelRenderer getWin() {
		return win;
	}
}
