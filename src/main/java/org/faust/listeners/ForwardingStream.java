package org.faust.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Phaser;

public class ForwardingStream implements Runnable {

    private final Phaser phaser;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ForwardingStream(Phaser phaser, InputStream inputStream, OutputStream outputStream) {
        this.phaser = phaser;
        this.phaser.register();
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + ": Started forwarding.");
        try {
            boolean active = true;
            while (active && phaser.getArrivedParties() == 0) {
                int firstByte = inputStream.read();
                if (firstByte != -1) {
                    outputStream.write(firstByte);
                    int available = inputStream.available();
                    if (available > 0) {
                        System.out.println(Thread.currentThread().getId() + ": Sending " + (available + 1) + " bytes.");
                        byte[] bytes = new byte[available];
                        inputStream.read(bytes);
                        outputStream.write(bytes);
                    } else {
                        System.out.println(Thread.currentThread().getId() + ": Sending 1 byte.");
                    }
                    outputStream.flush();
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
}
