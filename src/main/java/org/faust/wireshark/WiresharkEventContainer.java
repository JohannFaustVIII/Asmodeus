package org.faust.wireshark;

import org.faust.wireshark.token.DataToken;
import org.faust.wireshark.token.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WiresharkEventContainer {

    private static String CATALOG = "WIRESHARK";
    private static String fileSeparator = System.getProperty("file.separator");
    private String firstFilePath;
    private String secondFilePath;

    private WiresharkFileWriter firstFileWriter;
    private WiresharkFileWriter secondFileWriter;

    private boolean first = true;

    private final int counter;
    private final List<Integer> firstPacketSizes;
    private final List<Integer> secondPacketSizes;
    private Object lock = new Object();

    public WiresharkEventContainer(int counter) {
        startCatalog();
        this.counter = counter;
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
            WiresharkFileWriter current = first ? firstFileWriter : secondFileWriter;
            List<Integer> currentList = first ? firstPacketSizes : secondPacketSizes;

            Token token = new DataToken(event);
            current.saveTokenToFile(token);
            currentList.add(token.toBytes().length);

            if (currentList.size() >= counter) {
                WiresharkFileWriter another = !first ? firstFileWriter : secondFileWriter;
                List<Integer> anotherList = !first ? firstPacketSizes : secondPacketSizes;

                another.resetFile();
                anotherList.clear();
                first = !first;
            }
        }
    }

    public byte[] getPacketBytes() {
        synchronized (lock) {
            WiresharkFileWriter current = first ? firstFileWriter : secondFileWriter;
            List<Integer> currentList = first ? firstPacketSizes : secondPacketSizes;
            WiresharkFileWriter another = !first ? firstFileWriter : secondFileWriter;
            List<Integer> anotherList = !first ? firstPacketSizes : secondPacketSizes;

            int currentCount = currentList.size();
            int anotherCount = Math.min(counter - currentCount, anotherList.size());

            int anotherBytesToRead = anotherList.stream().mapToInt(Integer::valueOf).skip(anotherList.size() - anotherCount).sum();
            int currentBytesToRead = currentList.stream().mapToInt(Integer::valueOf).sum();

            byte[] result = new byte[anotherBytesToRead + currentBytesToRead];

            System.arraycopy(another.readLastBytes(anotherBytesToRead), 0, result, 0, anotherBytesToRead);
            System.arraycopy(current.readLastBytes(currentBytesToRead), 0, result, anotherBytesToRead, currentBytesToRead);

            return result;
        }
    }

    private static String getRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
