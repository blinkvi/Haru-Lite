package cc.unknown.util.client.network;

import cc.unknown.util.client.system.Clock;
import net.minecraft.network.Packet;

public class TimedPacket {

    public final Packet packet;
    public final Clock clock;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.clock = new Clock();
    }

    public TimedPacket(final Packet<?> packet, long stopWatchTime) {
        this.packet = packet;
        this.clock = new Clock();
        this.clock.hasPassed(stopWatchTime);
    }
}