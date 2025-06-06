package cc.unknown.util.client.network;

import cc.unknown.util.client.system.Clock;
import net.minecraft.network.Packet;

public class TimedPacket {

    public final Packet<?> packet;
    public final Clock stopWatch;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.stopWatch = new Clock();
    }

    public TimedPacket(final Packet<?> packet, long stopWatchTime) {
        this.packet = packet;
        this.stopWatch = new Clock();
        this.stopWatch.hasPassed(stopWatchTime);
    }
}