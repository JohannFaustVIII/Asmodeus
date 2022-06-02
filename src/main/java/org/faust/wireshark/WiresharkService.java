package org.faust.wireshark;

import org.faust.wireshark.token.RawDataToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WiresharkService {

    private final List<WiresharkEventHandler> eventHandlers = new ArrayList<>();

    public WiresharkService() {}

    public synchronized File getWsFile() {
        WiresharkFileWriter wiresharkFileWriter = getNewWiresharkFileWriter();
        writePacketsToFile(wiresharkFileWriter);
        return getFinishedFile(wiresharkFileWriter);
    }

    private WiresharkFileWriter getNewWiresharkFileWriter() {
        WiresharkFileWriter wiresharkFileWriter = null;
        try {
            wiresharkFileWriter = new WiresharkFileWriter("result");
        } catch (FileNotFoundException e) {
            System.err.println("Exception when getting wireshark file: " + e);
            e.printStackTrace();
        }
        wiresharkFileWriter.openFile();
        return wiresharkFileWriter;
    }

    private void writePacketsToFile(WiresharkFileWriter wiresharkFileWriter) {
        eventHandlers
                .stream()
                .map(WiresharkEventHandler::getBytes)
                .flatMap(bytes -> RawDataToken.getTokens(bytes).stream())
                .sorted(getDateComparator())
                .map(RawDataToken::getBytes)
                .forEach(wiresharkFileWriter::writeBytes);
    }

    private File getFinishedFile(WiresharkFileWriter wiresharkFileWriter) {
        wiresharkFileWriter.closeFile();
        return wiresharkFileWriter.getFile();
    }

    private Comparator<RawDataToken> getDateComparator() {
        return Comparator
                .comparingInt(RawDataToken::getSeconds)
                .thenComparingInt(RawDataToken::getMicroseconds);
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
