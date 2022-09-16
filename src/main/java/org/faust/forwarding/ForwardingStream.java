package org.faust.forwarding;

import lombok.Builder;
import org.faust.pcap.PcapEventHandler;
import org.faust.pcap.PcapForwardEvent;
import org.faust.statistics.ForwardingStats;
import org.faust.statistics.StatisticsService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Phaser;

public class ForwardingStream implements Runnable {

    private final Phaser phaser;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;
    private final String inIp;
    private final String outIp;
    private final int inPort;
    private final int outPort;
    private ForwardingStream linkedStream;

    private Thread currentThread; // TODO: to use to interrupt the thread

    @Builder
    public ForwardingStream(Phaser phaser, InputStream inputStream, OutputStream outputStream, StatisticsService statisticsService, PcapEventHandler pcapEventHandler, String inIp, String outIp, int inPort, int outPort) {
        this.phaser = phaser;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.statisticsService = statisticsService;
        this.pcapEventHandler = pcapEventHandler;
        this.inIp = inIp;
        this.outIp = outIp;
        this.inPort = inPort;
        this.outPort = outPort;
    }

    public void start() {
        currentThread = new Thread(this);
        currentThread.start();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + ": Started forwarding.");
        try {
            boolean active = true;
            while (active && phaser.getArrivedParties() == 0) {
                int forwarded = forwardBytes();
                if (forwarded != 0) {
                    statisticsService.add(new ForwardingStats(Thread.currentThread().getId(), forwarded));
                } else {
                    active = false;
                }
            }
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getId() + ": IOException thrown: " + e);
        } finally {
            turnOffLinked();
            phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getId() + ": Stopped forwarding.");
        }
    }

    private int forwardBytes() throws IOException {
        int count = 0;
        int firstByte = inputStream.read(); // this is locking operation, in theory
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

        pcapEventHandler.addEvent(new PcapForwardEvent(inIp, outIp, inPort, outPort, result));
    }

    public void linkStream(ForwardingStream forwardingStream) {
        this.linkedStream = forwardingStream;
        forwardingStream.linkedStream = this;
    }

    private void turnOffLinked() {
        if (linkedStream != null) {
            linkedStream.turnOff();
        }
    }

    private void turnOff() {
       currentThread.interrupt();
       // it should notify forwarder?
    }
}
