package org.faust.forwarding;

import lombok.Builder;
import org.faust.pcap.PcapEventHandler;
import org.faust.statistics.StatisticsService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Phaser;

@Builder
public class Forwarder {

    private final InputStream inInputStream;
    private final OutputStream inOutputStream;
    private final InputStream outInputStream;
    private final OutputStream outOutputStream;
    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;
    private final String outIp;
    private final String inIp;
    private final int outPort;
    private final int inPort;

    public void startForwarding() {
        Phaser phaser = new Phaser();
        ForwardingStream stream1 = new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(inInputStream)
                .outputStream(outOutputStream)
                .statisticsService(statisticsService)
                .pcapEventHandler(pcapEventHandler)
                .inIp(inIp)
                .outIp(outIp)
                .inPort(inPort)
                .outPort(outPort)
                .build();
        ForwardingStream stream2 = new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(outInputStream)
                .outputStream(inOutputStream)
                .statisticsService(statisticsService)
                .pcapEventHandler(pcapEventHandler)
                .inIp(outIp)
                .outIp(inIp)
                .inPort(outPort)
                .outPort(inPort)
                .build();
        stream1.linkStream(stream2);

        stream1.start();
        stream2.start();
    }
}
