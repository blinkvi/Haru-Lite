package cc.unknown.util.render.gif;

import java.awt.image.BufferedImage;

public class ImageFrame {

    private final BufferedImage image;
    private final int index, delay, width, height;

    public ImageFrame(BufferedImage image, int index, int delay) {
        this.image = image;
        this.index = index;
        this.delay = delay;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

	public BufferedImage getImage() {
		return image;
	}

	public int getIndex() {
		return index;
	}

	public int getDelay() {
		return delay;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}