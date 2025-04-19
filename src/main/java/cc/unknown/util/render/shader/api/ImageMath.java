package cc.unknown.util.render.shader.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageMath {
	public void premultiply(int[] p, int offset, int length) {
	    for (int i = offset; i < length; i++) {
	        int rgb = p[i];
	        int a = (rgb >> 24) & 0xFF;
	        int r = (rgb >> 16) & 0xFF;
	        int g = (rgb >> 8) & 0xFF;
	        int b = rgb & 0xFF;

	        float f = a * 0.003921569F;
	        p[i] = (a << 24) | ((int) (r * f) << 16) | ((int) (g * f) << 8) | (int) (b * f);
	    }
	}

	public void unpremultiply(int[] p, int offset, int length) {
	    for (int i = offset; i < length; i++) {
	        int rgb = p[i];
	        int a = (rgb >> 24) & 0xFF;
	        int r = (rgb >> 16) & 0xFF;
	        int g = (rgb >> 8) & 0xFF;
	        int b = rgb & 0xFF;

	        if (a != 0 && a != 255) {
	            float f = 255.0F / a;
	            r = Math.min(255, (int) (r * f));
	            g = Math.min(255, (int) (g * f));
	            b = Math.min(255, (int) (b * f));
	            p[i] = (a << 24) | (r << 16) | (g << 8) | b;
	        }
	    }
	}
}