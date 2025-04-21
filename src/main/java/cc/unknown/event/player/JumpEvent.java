package cc.unknown.event.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class JumpEvent extends Event {
	private float jumpMotion;
	private float yaw;
	public JumpEvent(float jumpMotion, float yaw) {
		this.jumpMotion = jumpMotion;
		this.yaw = yaw;
	}
	public float getJumpMotion() {
		return jumpMotion;
	}
	public void setJumpMotion(float jumpMotion) {
		this.jumpMotion = jumpMotion;
	}
	public float getYaw() {
		return yaw;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
}
