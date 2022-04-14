package org.faust.file;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Service
public class WSService {

    private final BlockingQueue<WSForwardEvent> queue = new SynchronousQueue<>(false);

    public void addForwarderBytes(WSForwardEvent event) {
        queue.offer(event);
    }

    public WSForwardEvent getEvent() throws InterruptedException {
        return queue.take();
    }

    public void processEvents() {
        new Thread( () -> {
            while (true) {
                WSForwardEvent event = null;
                try {
                    event = getEvent();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(event.getTimestamp() + ":Forwarded from " + event.getInIP() + " to " + event.getOutIP() + " bytes = " + Arrays.toString(event.getBytes()));
            }
        }).start();
    }

}
