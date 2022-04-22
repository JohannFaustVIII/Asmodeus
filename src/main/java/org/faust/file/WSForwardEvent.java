package org.faust.file;

public class WSForwardEvent {

    private final String inIP;
    private final String outIP;
    private final int inPort;
    private final int outPort;
    private final byte[] bytes;
    private final long timestamp;


    public WSForwardEvent(String inIP, String outIP, int inPort, int outPort, byte[] bytes) {
        this.inIP = inIP;
        this.outIP = outIP;
        this.inPort = inPort;
        this.outPort = outPort;
        this.bytes = bytes;
        this.timestamp = System.currentTimeMillis();
    }

    public String getInIP() {
        return inIP;
    }

    public String getOutIP() {
        return outIP;
    }

    public int getInPort() {
        return inPort;
    }

    public int getOutPort() {
        return outPort;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
