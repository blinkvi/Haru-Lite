package cc.unknown.event.player;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.move.MoveUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
@Getter
@Setter
public class StrafeEvent extends Event implements Accessor {

    private float forward;
    private float strafe;
    private float friction;
    private float yaw;
    private StrafeType strafeType;
	
    public StrafeEvent(StrafeType strafeType) {
    	this.strafeType = strafeType;
    }
	
	public boolean isPre() {
		return strafeType == StrafeType.PRE;
	}
	
	public boolean isPost() {
		return strafeType == StrafeType.POST;
	}
	
	public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        MoveUtil.stop();
    }
    
    public enum StrafeType {
    	PRE, POST;
    }
}
