package org.faust.forwarding;

public class ForwardConfig {

    private int inputPort;

    private int outputPort;

    private String outputIp;

    private int packetsCount;

    private int packetAge;

    public ForwardConfig() {
    }

    public ForwardConfig(int inputPort, int outputPort, String outputIp, int packetsCount, int packetAge) {
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.outputIp = outputIp;
        this.packetsCount = packetsCount;
        this.packetAge = packetAge;
    }

    public int getInputPort() {
        return inputPort;
    }

    public int getOutputPort() {
        return outputPort;
    }

    public String getOutputIp() {
        return outputIp;
    }

    public int getPacketsCount() {
        return packetsCount;
    }

    public int getPacketAge() {
        return packetAge;
    }

    @Override
    public String toString() {
        return "ForwardConfig{" +
                "inputPort=" + inputPort +
                ", outputPort=" + outputPort +
                ", outputIp='" + outputIp + '\'' +
                ", packetsCount=" + packetsCount +
                ", packetAge=" + packetAge +
                '}';
    }
}
