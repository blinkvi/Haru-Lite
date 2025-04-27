package cc.unknown.event;

public class CancellableEvent implements Event {

    private boolean canceled;

    public void setCanceled() {
        this.canceled = true;
    }

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean cancelled) {
		this.canceled = cancelled;
	}
}