package cc.unknown.util.render.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class AutoScaler {

    public static BufferedImage scaleImage(BufferedImage image) {
        if (image == null) return null;
        if (image.getWidth() <= 64 && image.getHeight() <= 64) return image;

        BufferedImage scaledImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = scaledImage.getGraphics();
        graphics.drawImage(image, 0, 0, 64, 64, null);
        graphics.dispose();

        return scaledImage;
    }
}