package org.faust.forwarding;

import lombok.Builder;
import org.faust.pcap.PcapEventHandler;
import org.faust.statistics.StatisticsService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

public class Forwarder { // TODO: should it ever exist? maybe its responsibility can be moved to Listener class?

    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;
    private final List<ForwardingStream> forwardingStreams = new ArrayList<>();

    @Builder
    public Forwarder(StatisticsService statisticsService, PcapEventHandler pcapEventHandler) {
        this.statisticsService = statisticsService;
        this.pcapEventHandler = pcapEventHandler;
    }

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

        forwardingStreams.add(stream1);
        forwardingStreams.add(stream2);
    }
}
