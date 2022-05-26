package org.faust.wireshark;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WiresharkService {

    private final List<WiresharkEventHandler> eventHandlers = new ArrayList<>();

    public WiresharkService() {}

    public File getWsFile() { //TODO: REFACTOR (need to map bytes to packets (flatMap), then sort by date, and then write)
        WiresharkFileWriter wiresharkFileWriter = null;
        try {
            wiresharkFileWriter = new WiresharkFileWriter("result");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        wiresharkFileWriter.openFile();
        for (WiresharkEventHandler eventHandler : eventHandlers) {
            wiresharkFileWriter.writeBytes(eventHandler.getBytes());
        }
        wiresharkFileWriter.closeFile();
        return wiresharkFileWriter.getFile();
    }

    public WiresharkEventHandler getHandler(int count) {
        synchronized (eventHandlers) {
            WiresharkEventHandler eventHandler = new WiresharkEventHandler(count, this::deregister);
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
