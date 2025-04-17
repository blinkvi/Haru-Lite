package cc.unknown.util.client.system;

public class StopWatch {
	public long millis;

	public StopWatch() {
		reset();
	}

	public boolean finished(long delay) {
		return System.currentTimeMillis() - delay >= millis;
	}

	public boolean elapse(double delay, boolean reset) {
		if ((double) (System.currentTimeMillis() - this.millis) >= delay) {
			if (reset) {
				this.reset();
			}

			return true;
		} else {
			return false;
		}
	}
	
    public boolean reached(final long lastTime, final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - millis + lastTime) >= currentTime;
    }

	public boolean finished() {
		return System.currentTimeMillis() >= millis;
	}

	public boolean reached(float millis) {
		return getElapsedTime() >= (millis);
	}

	public void reset() {
		this.millis = System.currentTimeMillis();
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - this.millis;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}
}