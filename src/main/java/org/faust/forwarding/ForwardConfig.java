package org.faust.forwarding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForwardConfig {

    private int inputPort;

    private int outputPort;

    private String outputIp;

    private int packetsCount;

    private int packetAge;

    private String forwardName;

    @Override
    public String toString() {
        return "ForwardConfig{" +
                "inputPort=" + inputPort +
                ", outputPort=" + outputPort +
                ", outputIp='" + outputIp + '\'' +
                ", packetsCount=" + packetsCount +
                ", packetAge=" + packetAge +
                ", forwardName='" + forwardName + '\'' +
                '}';
    }
}
