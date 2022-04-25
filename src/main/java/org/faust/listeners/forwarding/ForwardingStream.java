package org.faust.listeners.forwarding;

import org.faust.file.WSForwardEvent;
import org.faust.file.WSService;
import org.faust.stats.ForwardingStats;
import org.faust.stats.StatsService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Phaser;

public class ForwardingStream implements Runnable {

    private final Phaser phaser;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final StatsService statsService;
    private final WSService wsService;
    private final String inIp;
    private final String outIp;
    private final int inPort;
    private final int outPort;

    public ForwardingStream(ForwardingStreamBuilder builder) {
        this.phaser = builder.phaser;
        this.phaser.register();
        this.inputStream = builder.inputStream;
        this.outputStream = builder.outputStream;
        this.statsService = builder.statsService;
        this.wsService = builder.wsService;
        this.inIp = builder.inIp;
        this.outIp = builder.outIp;
        this.inPort = builder.inPort;
        this.outPort = builder.outPort;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + ": Started forwarding.");
        try {
            boolean active = true;
            while (active && phaser.getArrivedParties() == 0) {
                int forwarded = forwardBytes();
                if (forwarded != 0) {
                    statsService.add(new ForwardingStats(Thread.currentThread().getId(), forwarded));
                } else {
                    active = false;
                }
            }
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getId() + ": IOException thrown: " + e);
        } finally {
            phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getId() + ": Stopped forwarding.");
        }
    }

    private int forwardBytes() throws IOException {
        int count = 0;
        int firstByte = inputStream.read();
        if (firstByte != -1) {
            count++;
            outputStream.write(firstByte);
            int available = inputStream.available();
            byte[] bytes = new byte[0];
            if (available > 0) {
                bytes = new byte[available];
                int read = inputStream.read(bytes);
                count += read;
                outputStream.write(bytes, 0, read);
            }
            sendBytesToWS(firstByte, bytes);
            outputStream.flush();
        }
        return count;
    }

    private void sendBytesToWS(int firstByte, byte[] restOfBytes) {
        int size = 1 + restOfBytes.length;
        byte[] result = new byte[size];
        result[0] = (byte) firstByte;
        System.arraycopy(restOfBytes, 0, result, 1, restOfBytes.length);

        wsService.addForwarderBytes(new WSForwardEvent(inIp, outIp, inPort, outPort, result));
    }

    public static class ForwardingStreamBuilder {
        private Phaser phaser;
        private InputStream inputStream;
        private OutputStream outputStream;
        private StatsService statsService;
        private WSService wsService;
        private String inIp;
        private String outIp;
        private int inPort;
        private int outPort;

        public ForwardingStreamBuilder phaser(Phaser phaser) {
            this.phaser = phaser;
            return this;
        }

        public ForwardingStreamBuilder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public ForwardingStreamBuilder outputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
            return this;
        }

        public ForwardingStreamBuilder statsService(StatsService statsService) {
            this.statsService = statsService;
            return this;
        }

        public ForwardingStreamBuilder wsService(WSService wsService) {
            this.wsService = wsService;
            return this;
        }

        public ForwardingStreamBuilder inIp(String inIp) {
            this.inIp = inIp;
            return this;
        }

        public ForwardingStreamBuilder outIp(String outIp) {
            this.outIp = outIp;
            return this;
        }

        public ForwardingStreamBuilder inPort(int inPort) {
            this.inPort = inPort;
            return this;
        }

        public ForwardingStreamBuilder outPort(int outPort) {
            this.outPort = outPort;
            return this;
        }

        public ForwardingStream build() {
            return new ForwardingStream(this);
        }
    }
}
