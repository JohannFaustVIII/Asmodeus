package org.faust.stats;

public class ForwardingStats {

    private final long threadId;
    private final int count;
    private final long timestamp;

    public ForwardingStats(long threadId, int count) {
        this.threadId = threadId;
        this.count = count;
        this.timestamp = System.currentTimeMillis();
    }

    public long getThreadId() {
        return threadId;
    }

    public int getCount() {
        return count;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
