package cc.unknown.event.player;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@Getter
@Setter
public class SlowDownEvent extends Event {
    private float strafeMultiplier;
    private float forwardMultiplier;
    private boolean sprint;

    public SlowDownEvent(float strafeMultiplier, float forwardMultiplier) {
		this.strafeMultiplier = strafeMultiplier;
		this.forwardMultiplier = forwardMultiplier;
	}
}
