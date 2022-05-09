package org.faust.forwarding;

import org.faust.wireshark.WiresharkService;
import org.faust.statistics.StatisticsService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Phaser;

public class Forwarder {

    private final InputStream inInputStream;
    private final OutputStream inOutputStream;
    private final InputStream outInputStream;
    private final OutputStream outOutputStream;
    private final StatisticsService statisticsService;
    private final WiresharkService wiresharkService;
    private final String outIp;
    private final String inIp;
    private final int outPort;
    private final int inPort;

    private Forwarder(ForwarderBuilder builder) {
        inInputStream = builder.inInputStream;
        inOutputStream = builder.inOutputStream;

        outInputStream = builder.outInputStream;
        outOutputStream = builder.outOutputStream;

        this.statisticsService = builder.statisticsService;
        this.wiresharkService = builder.wiresharkService;
        this.outIp = builder.outIp;
        this.inIp = builder.inIp;

        this.outPort = builder.outPort;
        this.inPort = builder.inPort;
    }

    public void startForwarding() {
        Phaser phaser = new Phaser();
        Thread t1 = new Thread(new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(inInputStream)
                .outputStream(outOutputStream)
                .statsService(statisticsService)
                .wsService(wiresharkService)
                .inIp(inIp)
                .outIp(outIp)
                .inPort(inPort)
                .outPort(outPort)
                .build());
        Thread t2 = new Thread(new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(outInputStream)
                .outputStream(inOutputStream)
                .statsService(statisticsService)
                .wsService(wiresharkService)
                .inIp(inIp)
                .outIp(outIp)
                .inPort(outPort)
                .outPort(inPort)
                .build());
        t1.start();
        t2.start();
    }

    public static class ForwarderBuilder {
        private InputStream inInputStream;
        private OutputStream inOutputStream;
        private InputStream outInputStream;
        private OutputStream outOutputStream;
        private StatisticsService statisticsService;
        private WiresharkService wiresharkService;
        private String outIp;
        private String inIp;
        private int outPort;
        private int inPort;

        public ForwarderBuilder inInputStream(InputStream inInputStream) {
            this.inInputStream = inInputStream;
            return this;
        }

        public ForwarderBuilder inOutputStream(OutputStream inOutputStream) {
            this.inOutputStream = inOutputStream;
            return this;
        }

        public ForwarderBuilder outInputStream(InputStream outInputStream) {
            this.outInputStream = outInputStream;
            return this;
        }

        public ForwarderBuilder outOutputStream(OutputStream outOutputStream) {
            this.outOutputStream = outOutputStream;
            return this;
        }

        public ForwarderBuilder statsService(StatisticsService statisticsService) {
            this.statisticsService = statisticsService;
            return this;
        }

        public ForwarderBuilder wsService(WiresharkService wiresharkService) {
            this.wiresharkService = wiresharkService;
            return this;
        }

        public ForwarderBuilder outIp(String outIp) {
            this.outIp = outIp;
            return this;
        }

        public ForwarderBuilder inIp(String inIp) {
            this.inIp = inIp;
            return this;
        }

        public ForwarderBuilder outPort(int outPort) {
            this.outPort = outPort;
            return this;
        }

        public ForwarderBuilder inPort(int inPort) {
            this.inPort = inPort;
            return this;
        }

        public Forwarder build() {
            return new Forwarder(this);
        }
    }
}
