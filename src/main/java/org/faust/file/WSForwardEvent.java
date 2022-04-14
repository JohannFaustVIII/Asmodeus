package org.faust.file;

public class WSForwardEvent {

    private final String inIP;
    private final String outIP;
    private final byte[] bytes;
    private final long timestamp;

    public WSForwardEvent(String inIP, String outIP, byte[] bytes) {
        this.inIP = inIP;
        this.outIP = outIP;
        this.bytes = bytes;
        this.timestamp = System.currentTimeMillis();
    }

    public String getInIP() {
        return inIP;
    }

    public String getOutIP() {
        return outIP;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
