package org.faust.forwarding;

import lombok.Builder;
import org.faust.pcap.PcapEventHandler;
import org.faust.statistics.StatisticsService;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Phaser;

@Builder
public class Forwarder {

    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;

    public void startForwarding(Socket input, Socket output) throws IOException {
        // TODO: check addresses if correct
        Phaser phaser = new Phaser();
        ForwardingStream stream1 = new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(input.getInputStream())
                .outputStream(output.getOutputStream())
                .statisticsService(statisticsService)
                .pcapEventHandler(pcapEventHandler)
                .inIp(input.getInetAddress().getHostAddress())
                .outIp(output.getInetAddress().getHostAddress())
                .inPort(input.getPort())
                .outPort(output.getPort())
                .build();
        ForwardingStream stream2 = new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(output.getInputStream())
                .outputStream(input.getOutputStream())
                .statisticsService(statisticsService)
                .pcapEventHandler(pcapEventHandler)
                .inIp(output.getInetAddress().getHostAddress())
                .outIp(input.getInetAddress().getHostAddress())
                .inPort(output.getPort())
                .outPort(input.getPort())
                .build();
        stream1.linkStream(stream2);

        stream1.start();
        stream2.start();
    }
}
