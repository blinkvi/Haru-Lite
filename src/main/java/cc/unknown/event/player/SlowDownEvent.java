package cc.unknown.event.player;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SlowDownEvent extends Event {
    private float strafeMultiplier;
    private float forwardMultiplier;
    private boolean sprint;

    public SlowDownEvent(float strafeMultiplier, float forwardMultiplier) {
		this.strafeMultiplier = strafeMultiplier;
		this.forwardMultiplier = forwardMultiplier;
	}

	public float getStrafeMultiplier() {
		return strafeMultiplier;
	}

	public void setStrafeMultiplier(float strafeMultiplier) {
		this.strafeMultiplier = strafeMultiplier;
	}

	public float getForwardMultiplier() {
		return forwardMultiplier;
	}

	public void setForwardMultiplier(float forwardMultiplier) {
		this.forwardMultiplier = forwardMultiplier;
	}

	public boolean isSprint() {
		return sprint;
	}

	public void setSprint(boolean sprint) {
		this.sprint = sprint;
	}
}
