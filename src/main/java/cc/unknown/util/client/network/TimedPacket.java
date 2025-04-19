package cc.unknown.util.client.network;

import cc.unknown.util.client.system.StopWatch;
import net.minecraft.network.Packet;

public class TimedPacket {

    private final Packet<?> packet;
    private final StopWatch stopWatch;

    public TimedPacket(Packet<?> packet) {
        this.packet = packet;
        this.stopWatch = new StopWatch();
    }

    public TimedPacket(final Packet<?> packet, long stopWatchTime) {
        this.packet = packet;
        this.stopWatch = new StopWatch();
        this.stopWatch.hasPassed(stopWatchTime);
    }

	public Packet<?> getPacket() {
		return packet;
	}

	public StopWatch getStopWatch() {
		return stopWatch;
	}
}