package org.faust.listeners.forwarding;

import org.faust.file.WSService;
import org.faust.stats.StatsService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Phaser;

public class Forwarder {

    private final InputStream inInputStream;
    private final OutputStream inOutputStream;
    private final InputStream outInputStream;
    private final OutputStream outOutputStream;
    private final StatsService statsService;
    private final WSService wsService;
    private final String outIp;
    private final String inIp;
    private final int outPort;
    private final int inPort;

    private Forwarder(ForwarderBuilder builder) throws IOException {
        inInputStream = builder.inInputStream;
        inOutputStream = builder.inOutputStream;

        outInputStream = builder.outInputStream;
        outOutputStream = builder.outOutputStream;

        this.statsService = builder.statsService;
        this.wsService = builder.wsService;
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
                .statsService(statsService)
                .wsService(wsService)
                .inIp(inIp)
                .outIp(outIp)
                .inPort(inPort)
                .outPort(outPort)
                .build());
        Thread t2 = new Thread(new ForwardingStream.ForwardingStreamBuilder()
                .phaser(phaser)
                .inputStream(outInputStream)
                .outputStream(inOutputStream)
                .statsService(statsService)
                .wsService(wsService)
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
        private StatsService statsService;
        private WSService wsService;
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

        public ForwarderBuilder statsService(StatsService statsService) {
            this.statsService = statsService;
            return this;
        }

        public ForwarderBuilder wsService(WSService wsService) {
            this.wsService = wsService;
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

        public Forwarder build() throws IOException {
            return new Forwarder(this);
        }
    }
}
