package cc.unknown.util.render.anisimple;

public final class AnimationUtils {
	public static float calculateCompensationA(float target, float current, double speed, long delta) {
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

	public static float calculateCompensationB(float target, float current, double speed, long delta) {
		float diff = current - target;
		double add = delta * speed / 50.0D;
		if (Math.abs(diff) > speed) {
			if (diff > 0.0F) {
				current = (float) (current - add);
			} else {
				current = (float) (current + add);
			}
		} else {
			double adjustment = add * Math.signum(diff);
			if (Math.abs(adjustment) > Math.abs(diff))
				adjustment = -diff;
			current = (float) (current - adjustment);
		}
		return current;
	}
}
