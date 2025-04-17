package cc.unknown.ui.click.complement;

import cc.unknown.util.Accessor;

public interface IComponent extends Accessor {
    default void drawScreen(int mouseX, int mouseY) {
    }

    default void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    default void mouseReleased(int mouseX, int mouseY, int state) {
    }

    default void keyTyped(char typedChar, int keyCode) {
    }
}