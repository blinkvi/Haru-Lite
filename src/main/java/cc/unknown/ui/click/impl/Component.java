package cc.unknown.ui.click.impl;

import cc.unknown.util.Accessor;

public class Component implements Accessor {

	public float x, y, width, height;

	public boolean isVisible() {
		return true;
	}

	public void drawScreen(int mouseX, int mouseY) {
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
	}

	public void keyTyped(char typedChar, int keyCode) {
	}
}