package org.faust.wireshark;

import org.faust.environment.EnvironmentService;
import org.faust.wireshark.token.DataToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class WiresharkService {

    private final Queue<WiresharkForwardEvent> queue = new ConcurrentLinkedQueue<>();

    private final List<WiresharkEventHandler> eventHandlers = new ArrayList<>();

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
        queue.add(event);
    }

    public WiresharkForwardEvent getEvent() throws InterruptedException {
        return queue.poll();
    }

    public void processEvents() {
        wiresharkFileWriter.openFile();
        new Thread( () -> {
            while (true) {
                WiresharkForwardEvent event = null;
                try {
                    while ((event = getEvent()) == null); // would be better to have it blocking?
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

    public WiresharkEventHandler getHandler() {
        synchronized (eventHandlers) {
            WiresharkEventHandler eventHandler = new WiresharkEventHandler(this::deregister);
            eventHandlers.add(eventHandler);
            return eventHandler;
        }
    }

    public void deregister(WiresharkEventHandler eventHandler) {
        synchronized (eventHandlers) {
            eventHandlers.remove(eventHandler);
        }
    }
}
