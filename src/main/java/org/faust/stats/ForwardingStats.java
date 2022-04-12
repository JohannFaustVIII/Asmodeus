package org.faust.stats;

public class ForwardingStats {

    private static final String LOG_PATTERN = "ThreadId: %d\t timestamp: %d\t read: %d";

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

    public String getLogMessage() {
        return String.format(LOG_PATTERN,
                threadId,
                timestamp,
                count);
    }

    @Override
    public String toString() {
        return "ForwardingStats{" +
                "threadId=" + threadId +
                ", count=" + count +
                ", timestamp=" + timestamp +
                '}';
    }
}
