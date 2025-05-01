package cc.unknown.event.player;
import cc.unknown.util.Accessor;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class StrafeEvent extends Event implements Accessor {

	public float forward;
    public float strafe;
    public float friction;
    public float yaw;
    public StrafeType strafeType;
	
    public StrafeEvent(float forward, float strafe, float friction, float yaw, StrafeType strafeType) {
		this.forward = forward;
		this.strafe = strafe;
		this.friction = friction;
		this.yaw = yaw;
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
    
    public enum StrafeType {
    	PRE, POST;
    }
}
