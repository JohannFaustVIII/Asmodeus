package org.faust.wireshark;

import org.faust.wireshark.token.RawDataToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Service
public class WiresharkService {

    private final Map<String, WiresharkEventHandler> eventHandlers = new HashMap<>();

    public WiresharkService() {}

    public synchronized File getWsFile() {
        WiresharkFileWriter wiresharkFileWriter = getNewWiresharkFileWriter();
        writePacketsToFile(wiresharkFileWriter);
        return getFinishedFile(wiresharkFileWriter);
    }

    public synchronized File getSpecifiedWsFile(String name) {
        WiresharkFileWriter wiresharkFileWriter = getNewWiresharkFileWriter();
        writeSpecifiedPacketsToFile(wiresharkFileWriter, name);
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
                .values()
                .stream()
                .flatMap(handler -> handler.getRawPackets().stream())
                .sorted(getDateComparator())
                .map(RawDataToken::getBytes)
                .forEach(wiresharkFileWriter::writeBytes);
    }

    private void writeSpecifiedPacketsToFile(WiresharkFileWriter wiresharkFileWriter, String name) {
        eventHandlers
                .get(name)
                .getRawPackets()
                .stream()
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

    public WiresharkEventHandler getHandler(int count, int packetAge, String forwardName) {
        synchronized (eventHandlers) {
            if (eventHandlers.containsKey(forwardName)) {
                throw new WiresharkServiceException("Existing forwardName tried to be initialized: " + forwardName);
            }

            WiresharkEventHandler eventHandler = new WiresharkEventHandler(count, packetAge, this::deregister);
            eventHandlers.put(forwardName, eventHandler);
            return eventHandler;
        }
    }

    public void deregister(WiresharkEventHandler eventHandler) {
        synchronized (eventHandlers) {
            eventHandlers.remove(eventHandler);
        }
    }

    public static class WiresharkServiceException extends RuntimeException { // TODO: refactor maybe

        public WiresharkServiceException(String message) {
            super(message);
        }
    }
}
