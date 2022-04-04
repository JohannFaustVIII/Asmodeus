package org.faust.listeners.forwarding;

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

    public ForwardingStream(Phaser phaser, InputStream inputStream, OutputStream outputStream, StatsService statsService) {
        this.phaser = phaser;
        this.phaser.register();
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.statsService = statsService;
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
            if (available > 0) {
                byte[] bytes = new byte[available];
                int read = inputStream.read(bytes);
                count += read;
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
        }
        return count;
    }
}
