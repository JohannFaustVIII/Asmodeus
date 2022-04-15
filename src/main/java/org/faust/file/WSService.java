package org.faust.file;

import org.faust.config.EnvironmentService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Service
public class WSService {

    private final BlockingQueue<WSForwardEvent> queue = new SynchronousQueue<>(false);

    private final EnvironmentService envService;

    private WSFileWriter wsFileWriter;

    public WSService(EnvironmentService envService) {
        this.envService = envService;
        try {
            wsFileWriter = new WSFileWriter(envService.getWSFilePath());
        } catch (FileNotFoundException e) {
            System.err.println("Failed to initialize WS fileWriter");
            e.printStackTrace();
        }
    }

    public void addForwarderBytes(WSForwardEvent event) {
        queue.offer(event);
    }

    public WSForwardEvent getEvent() throws InterruptedException {
        return queue.take();
    }

    public void processEvents() {
        wsFileWriter.openFile();
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

    public File getWsFile() {
        return wsFileWriter.getFile();
    }
}
