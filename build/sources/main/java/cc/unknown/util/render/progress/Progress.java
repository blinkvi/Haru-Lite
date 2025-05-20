package cc.unknown.util.render.progress;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import cc.unknown.util.Accessor;
import cc.unknown.util.render.Animation;
import cc.unknown.util.render.enums.animation.Easing;
import cc.unknown.util.render.font.FontUtil;
import cc.unknown.util.render.shader.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;

public class Progress implements Accessor {
    private final Animation posYAnimation = new Animation(Easing.EASE_OUT_EXPO, 300);
    private int posY;
    private double progress = 0;
    private String text;

    private final List<Runnable> preRender = new ArrayList<>();

    public Progress(int posY, String text) {
        this.posY = posY;
        this.text = text;
        posYAnimation.setValue(this.posY);
    }

    public Progress(String text) {
        this(0, text);
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void setProgress(Supplier<Double> progress) {
        setProgress(progress.get());
    }

    public void registerPreRender(Runnable... actions) {
        Collections.addAll(preRender, actions);
    }

    public void render() {
        preRender.forEach(Runnable::run);

        posYAnimation.run(posY);

        if (mc.currentScreen != null) return;

        final ScaledResolution sr = new ScaledResolution(mc);
        final float width = sr.getScaledWidth() / 5;
        final float height = width / 13;
        final float renderY = (float) (sr.getScaledHeight() * 0.8 - posYAnimation.getValue() * height * 1.5);

        // background
        RoundedUtil.drawRound(
                sr.getScaledWidth() / 2.0f - width / 2.0f,
                renderY - height / 2.0f,
                width, height, height / 2.0f, new Color(255, 255, 255, 30)
        );

        // progress
        RoundedUtil.drawRound(
                sr.getScaledWidth() / 2.0f - width / 2.0f,
                renderY - height / 2.0f,
                (float) (width * progress), height, height / 2.0f,
                new Color(6, 112, 190, 200)
        );

        // text
        FontUtil.getFontRenderer("interMedium.ttf", 16).drawCenteredString(text, sr.getScaledWidth() / 2.0 + 4, renderY - 2, new Color(240, 240, 240).getRGB());
    }

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Animation getPosYAnimation() {
		return posYAnimation;
	}

	public double getProgress() {
		return progress;
	}

	public List<Runnable> getPreRender() {
		return preRender;
	}
}
