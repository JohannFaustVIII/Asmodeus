package org.faust.wireshark;

import java.io.File;
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

    private final int counter;
    private final List<Integer> firstPacketSizes;
    private final List<Integer> secondPacketSizes;

    public WiresharkEventContainer(int counter) {
        this.counter = counter;
        firstPacketSizes = new ArrayList<>(counter);
        secondPacketSizes = new ArrayList<>(counter);

        String path = generateUniquePath();
        generateUniqueFiles(path);
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

    private static String getRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
