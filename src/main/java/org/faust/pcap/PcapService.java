package org.faust.pcap;

import org.faust.pcap.token.RawDataToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class PcapService {

    private final Map<String, PcapEventHandler> eventHandlers = new HashMap<>();

    public PcapService() {}

    public Set<String> getHandlers() {
        synchronized (eventHandlers) {
            return eventHandlers.keySet();
        }
    }

    public synchronized File getWsFile() {
        PcapFileWriter pcapFileWriter = getNewPcapFileWriter();
        writePacketsToFile(pcapFileWriter);
        return getFinishedFile(pcapFileWriter);
    }

    public synchronized File getSpecifiedWsFile(String name) {
        PcapFileWriter pcapFileWriter = getNewPcapFileWriter();
        writeSpecifiedPacketsToFile(pcapFileWriter, name);
        return getFinishedFile(pcapFileWriter);
    }

    private PcapFileWriter getNewPcapFileWriter() {
        PcapFileWriter pcapFileWriter = null;
        try {
            pcapFileWriter = new PcapFileWriter("result");
        } catch (FileNotFoundException e) {
            System.err.println("Exception when getting pcap file: " + e);
            e.printStackTrace();
        }
        pcapFileWriter.openFile();
        return pcapFileWriter;
    }

    private void writePacketsToFile(PcapFileWriter pcapFileWriter) {
        eventHandlers
                .values()
                .stream()
                .flatMap(handler -> handler.getRawPackets().stream())
                .sorted(getDateComparator())
                .map(RawDataToken::getBytes)
                .forEach(pcapFileWriter::writeBytes);
    }

    private void writeSpecifiedPacketsToFile(PcapFileWriter pcapFileWriter, String name) {
        eventHandlers
                .get(name)
                .getRawPackets()
                .stream()
                .map(RawDataToken::getBytes)
                .forEach(pcapFileWriter::writeBytes);
    }

    private File getFinishedFile(PcapFileWriter pcapFileWriter) {
        pcapFileWriter.closeFile();
        return pcapFileWriter.getFile();
    }

    private Comparator<RawDataToken> getDateComparator() {
        return Comparator
                .comparingInt(RawDataToken::getSeconds)
                .thenComparingInt(RawDataToken::getMicroseconds);
    }

    public PcapEventHandler getHandler(int count, int packetAge, String forwardName) {
        synchronized (eventHandlers) {
            if (eventHandlers.containsKey(forwardName)) {
                throw new PcapServiceException("Existing forwardName tried to be initialized: " + forwardName);
            }

            PcapEventHandler eventHandler = new PcapEventHandler(count, packetAge, this::deregister);
            eventHandlers.put(forwardName, eventHandler);
            return eventHandler;
        }
    }

    public void deregister(PcapEventHandler eventHandler) {
        synchronized (eventHandlers) {
            eventHandlers.remove(eventHandler);
        }
    }

}
