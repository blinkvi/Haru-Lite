package cc.unknown.util.client.network;

import java.util.Arrays;

import cc.unknown.util.structure.RollingArrayLongBuffer;

public class PPSCounter {
    private static final RollingArrayLongBuffer[] TIMESTAMP_BUFFERS = new RollingArrayLongBuffer[PacketType.values().length];

    static {
        Arrays.setAll(TIMESTAMP_BUFFERS, i -> new RollingArrayLongBuffer(99999));
    }

    public static void registerType(PacketType type) {
        TIMESTAMP_BUFFERS[type.ordinal()].add(System.currentTimeMillis());
    }

    public static int getPPS(PacketType type) {
        return TIMESTAMP_BUFFERS[type.ordinal()].getTimestampsSince(System.currentTimeMillis() - 1000L);
    }

    public enum PacketType {
        SEND, RECEIVED
    }
}