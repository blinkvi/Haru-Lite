package cc.unknown.event.player;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SlowDownEvent extends Event {
	public float strafeMultiplier;
	public float forwardMultiplier;
	public boolean sprint;

    public SlowDownEvent(float strafeMultiplier, float forwardMultiplier) {
		this.strafeMultiplier = strafeMultiplier;
		this.forwardMultiplier = forwardMultiplier;
	}
}
