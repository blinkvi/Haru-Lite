package cc.unknown.util.render.anisimple;

import net.minecraft.util.MathHelper;

public final class SimpleAnimation {
	private float value;

	private long lastMS;

	public SimpleAnimation(float value) {
		this.value = value;
		this.lastMS = System.currentTimeMillis();
	}

	public void setAnimation(float value, double speed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - this.lastMS;
		this.lastMS = currentMS;
		double deltaValue = 0.0D;
		if (speed > 28.0D)
			speed = 28.0D;
		if (speed != 0.0D)
			deltaValue = (Math.abs(value - this.value) * 0.35F) / 10.0D / speed;
		this.value = calculateCompensation(value, this.value, deltaValue, delta);
	}

	public void setAnimation(float from, float to, double speed) {
		this.lastMS = System.currentTimeMillis();
		double deltaValue = 0.0D;
		if (speed > 28.0D)
			speed = 28.0D;
		if (speed != 0.0D)
			deltaValue = Math.abs(to - from) * 0.35D / 10.0D / speed;
		if (from < to) {
			this.value = from + (float) deltaValue;
		} else {
			this.value = from - (float) deltaValue;
		}
	}

	public void setAnimationWrap180(float from, float to, float speed) {
		float angle = MathHelper.wrapAngleTo180_float(to - from);
		setAnimation(from, from + angle, speed);
	}

	public float lerp(float from, float to, float percent) {
		return from + (to - from) * percent;
	}

	public float getValueF() {
		return this.value;
	}

	public int getValueI() {
		return (int) this.value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void reset() {
		reset(0.0F);
	}

	public void reset(float value) {
		this.value = value;
		this.lastMS = System.currentTimeMillis();
	}
	
	private float calculateCompensation(float target, float current, double speed, long delta) {
		float diff = current - target;
		double add = delta * speed / 50.0D;
		if (diff > speed) {
			if (current - add > target) {
				current = (float) (current - add);
			} else {
				current = target;
			}
		} else if (diff < -speed) {
			if (current + add < target) {
				current = (float) (current + add);
			} else {
				current = target;
			}
		} else {
			current = target;
		}
		return current;
	}
}
