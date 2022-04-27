package org.faust.config;

public class ForwardConfig {

    private int inputPort;

    private int outputPort;

    private String outputIp;

    public ForwardConfig() {
    }

    public ForwardConfig(int inputPort, int outputPort, String outputIp) {
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.outputIp = outputIp;
    }

    public int getInputPort() {
        return inputPort;
    }

    public void setInputPort(int inputPort) {
        this.inputPort = inputPort;
    }

    public int getOutputPort() {
        return outputPort;
    }

    public void setOutputPort(int outputPort) {
        this.outputPort = outputPort;
    }

    public String getOutputIp() {
        return outputIp;
    }

    public void setOutputIp(String outputIp) {
        this.outputIp = outputIp;
    }

    @Override
    public String toString() {
        return "ForwardConfig{" +
                "inputPort=" + inputPort +
                ", outputPort=" + outputPort +
                ", outputIp='" + outputIp + '\'' +
                '}';
    }
}
