package cc.unknown.event.player;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.move.MoveUtil;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class StrafeEvent extends Event implements Accessor {

    private float forward;
    private float strafe;
    private float friction;
    private float yaw;
    private StrafeType strafeType;
	
    public StrafeEvent(float forward, float strafe, float friction, float yaw, StrafeType strafeType) {
		this.forward = forward;
		this.strafe = strafe;
		this.friction = friction;
		this.yaw = yaw;
		this.strafeType = strafeType;
	}

	public float getForward() {
		return forward;
	}

	public void setForward(float forward) {
		this.forward = forward;
	}

	public float getStrafe() {
		return strafe;
	}

	public void setStrafe(float strafe) {
		this.strafe = strafe;
	}

	public float getFriction() {
		return friction;
	}

	public void setFriction(float friction) {
		this.friction = friction;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public StrafeType getStrafeType() {
		return strafeType;
	}

	public void setStrafeType(StrafeType strafeType) {
		this.strafeType = strafeType;
	}

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
