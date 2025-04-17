package cc.unknown.util.structure;

public class RollingArrayLongBuffer {
    private final long[] contents;
    private int currentIndex = 0;

    public RollingArrayLongBuffer(int length) {
        this.contents = new long[length];
    }

    public void add(long l) {
        currentIndex = (currentIndex + 1) % contents.length;
        contents[currentIndex] = l;
    }

    public int getTimestampsSince(long l) {
        for (int i = 0; i < contents.length; i++) {
            int index = (currentIndex < i) ? contents.length - i + currentIndex : currentIndex - i;
            if (contents[index] < l) {
                return i;
            }
        }
        return contents.length;
    }
}
