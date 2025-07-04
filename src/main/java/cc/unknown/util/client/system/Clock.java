package cc.unknown.util.client.system;

public class Clock {
    private long startTime;

    public Clock() {
        reset();
    }
    
    public Clock(long lasts){
        this.startTime = lasts;
    }
    
    public boolean hasPassed(long delay) {
        return System.currentTimeMillis() - delay >= startTime;
    }
    
    public boolean hasElapsed(double delay, boolean reset) {
        if (System.currentTimeMillis() - this.startTime >= delay) {
            if (reset) reset();
            return true;
        }
        return false;
    }

    public boolean hasElapsedMillis(long millis, boolean reset) {
        if (hasPassedMillis(millis)) {
            if (reset) reset();
            return true;
        }
        return false;
    }

    public boolean hasPassedMillis(long millis) {
        return getElapsedTime() >= millis;
    }

    public boolean hasElapsedTicks(int ticks, boolean reset) {
        return hasElapsedMillis(ticks * 50L, reset);
    }

    public boolean hasPassedTicks(int ticks) {
        return getElapsedTime() >= ticks * 50L;
    }

    public boolean reachedSince(long lastTime, long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - startTime + lastTime) >= currentTime;
    }

    public boolean reached(float millis) {
        return getElapsedTime() >= millis;
    }
    
    public boolean floating(float millis) {
        return getTime() >= millis;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= startTime;
    }
    
    public long getTime() {
        return Math.max(0L, System.currentTimeMillis() - startTime);
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}