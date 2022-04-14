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

    public ForwardingStream(Phaser phaser, InputStream inputStream, OutputStream outputStream, StatsService statsService, WSService wsService, String inIp, String outIp) {
        this.phaser = phaser;
        this.phaser.register();
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.statsService = statsService;
        this.wsService = wsService;
        this.inIp = inIp;
        this.outIp = outIp;
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

        wsService.addForwarderBytes(new WSForwardEvent(inIp, outIp, result));
    }
}
