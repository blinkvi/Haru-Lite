package cc.unknown.util.render.shader.impl;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cc.unknown.util.Accessor;
import net.minecraft.client.renderer.texture.TextureUtil;

public class GradientBlur implements Accessor {
    private float x, y;
    private int width, height, delay;
    private final Timer timer = new Timer();

    public void set(float x, float y, int width, int height, int delay) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setDelay(delay);
    }

    public void getPixels() {
        if (timer.hasReach(delay)) {
            IntBuffer pixelBuffer;
            int[] pixelValues;
            int size = width * height;
            pixelBuffer = BufferUtils.createIntBuffer(size);
            pixelValues = new int[size];
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();
            int scaleFactor = 1;
            int k = mc.gameSettings.guiScale;
            if (k == 0) {
                k = 1000;
            }
            while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
                    && mc.displayHeight / (scaleFactor + 1) >= 240) {
                ++scaleFactor;
            }
            GL11.glReadPixels((int) (x * scaleFactor), (int) ((mc.displayHeight - (y + 6) * scaleFactor)), width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            timer.reset();
        }
    }
	
    public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public Timer getTimer() {
		return timer;
	}

	public class Timer {
        private long lastCheck = getSystemTime();

        public boolean hasReach(float mil) {
            return getTimePassed() >= (mil);
        }

        public void reset() {
            lastCheck = getSystemTime();
        }

        private long getTimePassed() {
            return getSystemTime() - lastCheck;
        }

        private long getSystemTime() {
            return System.nanoTime() / (long) (1E6);
        }
    }
}