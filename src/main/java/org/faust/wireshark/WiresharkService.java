package org.faust.wireshark;

import org.faust.environment.EnvironmentService;
import org.faust.wireshark.token.DataToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Service
public class WiresharkService {

    private final BlockingQueue<WiresharkForwardEvent> queue = new SynchronousQueue<>(false);

    private final EnvironmentService envService;

    private WiresharkFileWriter wiresharkFileWriter;

    public WiresharkService(EnvironmentService envService) {
        this.envService = envService;
        try {
            wiresharkFileWriter = new WiresharkFileWriter(envService.getWSFilePath());
        } catch (FileNotFoundException e) {
            System.err.println("Failed to initialize WS fileWriter");
            e.printStackTrace();
        }
    }

    public void addForwarderBytes(WiresharkForwardEvent event) {
        queue.offer(event);
    }

    public WiresharkForwardEvent getEvent() throws InterruptedException {
        return queue.take();
    }

    public void processEvents() {
        wiresharkFileWriter.openFile();
        new Thread( () -> {
            while (true) {
                WiresharkForwardEvent event = null;
                try {
                    event = getEvent();
                    wiresharkFileWriter.saveTokenToFile(new DataToken(event));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(event.getTimestamp() + ":Forwarded from " + event.getInIP() + " to " + event.getOutIP() + " bytes = " + Arrays.toString(event.getBytes()));
            }
        }).start();
    }

    public File getWsFile() {
        return wiresharkFileWriter.getFile();
    }
}
