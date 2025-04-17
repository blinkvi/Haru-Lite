package cc.unknown.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
@Getter
@Setter
public class PrePositionEvent extends Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround, isSprinting, isSneaking;
}