package org.faust.wireshark;

import org.faust.wireshark.token.DataToken;
import org.faust.wireshark.token.RawDataToken;
import org.faust.wireshark.token.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WiresharkEventContainer {

    private static String CATALOG = "WIRESHARK";
    private static String fileSeparator = System.getProperty("file.separator");
    private String firstFilePath;
    private String secondFilePath;

    private WiresharkFileWriter firstFileWriter;
    private WiresharkFileWriter secondFileWriter;

    private boolean first = true;

    private final int counter;
    private final int packetAge;
    private final List<Integer> firstPacketSizes;
    private final List<Integer> secondPacketSizes;
    private Object lock = new Object();

    public WiresharkEventContainer(int counter, int packetAge) {
        startCatalog();
        this.counter = counter;
        this.packetAge = packetAge;
        firstPacketSizes = new ArrayList<>(counter);
        secondPacketSizes = new ArrayList<>(counter);

        String path = generateUniquePath();
        generateUniqueFiles(path);

        try {
            firstFileWriter = new WiresharkFileWriter(firstFilePath);
            secondFileWriter = new WiresharkFileWriter(secondFilePath);
            firstFileWriter.openFile();
            secondFileWriter.openFile();
        } catch (FileNotFoundException ex) {
            System.err.println("WiresharkEventContainer didn't find a file, ex: " + ex);
            ex.printStackTrace();
        }
    }

    private void startCatalog() {
        synchronized (CATALOG) {
            if (!Files.exists(Paths.get(CATALOG))) {
                new File(CATALOG).mkdir();
            }
        }
    }

    private String generateUniquePath() {
        synchronized (CATALOG) {
            String path;
            do {
                path = CATALOG + fileSeparator + getRandomString();
            } while (Files.exists(Paths.get(path)));
            new File(path).mkdir();
            return path;
        }
    }

    private void generateUniqueFiles(String path) {
        String firstPath;
        String secondPath;
        do {
            firstPath = path + fileSeparator + getRandomString();
            secondPath = path + fileSeparator + getRandomString();
        } while (firstPath.equals(secondPath));
        firstFilePath = firstPath;
        secondFilePath = secondPath;
    }

    public void addEvent(WiresharkForwardEvent event) {
        synchronized (lock) {
            Token token = new DataToken(event);
            putTokenInFile(token);
            saveTokenSize(token);
            switchFileIfNeeded();
        }
    }

    private void putTokenInFile(Token token) {
        WiresharkFileWriter current = getPrimaryFileWriter();
        current.saveTokenToFile(token);
    }

    private void saveTokenSize(Token token) {
        List<Integer> currentList = getPrimaryByteCountList();
        currentList.add(token.toBytes().length);
    }

    private void switchFileIfNeeded() {
        if (getPrimaryByteCountList().size() >= counter) {
            WiresharkFileWriter another = getSecondaryFileWriter();
            List<Integer> anotherList = getSecondaryByteCountList();

            another.resetFile();
            anotherList.clear();
            first = !first;
        }
    }

    public List<RawDataToken> getRawPackets() {
        synchronized (lock) {
            WiresharkFileWriter current = getPrimaryFileWriter();
            List<Integer> currentList = getPrimaryByteCountList();
            WiresharkFileWriter another = getSecondaryFileWriter();
            List<Integer> anotherList = getSecondaryByteCountList();

            int currentCount = currentList.size();
            int anotherCount = Math.min(counter - currentCount, anotherList.size());

            int anotherBytesToRead = anotherList.stream().mapToInt(Integer::valueOf).skip(anotherList.size() - anotherCount).sum();
            int currentBytesToRead = currentList.stream().mapToInt(Integer::valueOf).sum();

            byte[] result = new byte[anotherBytesToRead + currentBytesToRead];

            System.arraycopy(another.readLastBytes(anotherBytesToRead), 0, result, 0, anotherBytesToRead);
            System.arraycopy(current.readLastBytes(currentBytesToRead), 0, result, anotherBytesToRead, currentBytesToRead);

            long time = System.currentTimeMillis();
            long age = 1_000L * packetAge;

            return RawDataToken
                    .getTokens(result)
                    .stream()
                    .filter(packet -> time - packet.getTime() <= age)
                    .collect(Collectors.toList());
        }
    }

    private WiresharkFileWriter getPrimaryFileWriter() {
        return first ? firstFileWriter : secondFileWriter;
    }

    private List<Integer> getPrimaryByteCountList() {
        return first ? firstPacketSizes : secondPacketSizes;
    }

    private WiresharkFileWriter getSecondaryFileWriter() {
        return !first ? firstFileWriter : secondFileWriter;
    }

    private List<Integer> getSecondaryByteCountList() {
        return !first ? firstPacketSizes : secondPacketSizes;
    }

    private static String getRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
