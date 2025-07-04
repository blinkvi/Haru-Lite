package cc.unknown.util.client.network;

import cc.unknown.util.client.system.Clock;
import net.minecraft.network.Packet;

public class TimedPacket {

    public final Packet<?> packet;
    public final Clock time;
    public final long millis;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.time = new Clock();
        this.millis = System.currentTimeMillis();
    }

    public TimedPacket(final Packet<?> packet, final long millis) {
        this.packet = packet;
        this.millis = millis;
        this.time = new Clock();
    }

}