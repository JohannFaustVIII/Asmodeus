package org.faust.wireshark;

import org.faust.wireshark.token.HeaderToken;
import org.faust.wireshark.token.Token;

import java.io.*;

public class WiresharkFileWriter {

    private final String filePath;
    private File outputFile;
    private OutputStream outputStream;

    public WiresharkFileWriter(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        openFileOutputStream();
    }

    public void openFile() {
        saveTokenToFile(new HeaderToken());
    }

    private void openFileOutputStream() throws FileNotFoundException {
        outputFile = new File(filePath);
        outputStream = new FileOutputStream(outputFile);
    }

    public void saveTokenToFile(Token token) {
        writeBytes(token.toBytes());
    }

    public void writeBytes(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Exception during writing bytes to file: " + e);
            e.printStackTrace();
        }
    }

    public File getFile() {
        return outputFile;
    }

    public void resetFile() {
        try {
            outputStream.close();
            outputStream = new FileOutputStream(outputFile);
            openFile();
        } catch (IOException e) {
            System.err.println("Exception during resetting file: " + e);
            e.printStackTrace();
        }
    }

    public byte[] readLastBytes(int count) {
        try {
            try (RandomAccessFile readFile = new RandomAccessFile(outputFile, "r")) {
                byte[] result = new byte[count];
                readFile.seek(outputFile.length() - count);
                readFile.read(result, 0, count);
                return result;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Exception with finding file during reading last bytes: " + e);
            e.printStackTrace();
            return new byte[0];
        } catch (IOException e) {
            System.err.println("Exception during reading last bytes: " + e);
            e.printStackTrace();
            return new byte[0];
        }
    }

    public void closeFile() {
        try {
            outputStream.close();
        } catch (IOException e) {
            System.err.println("Exception during closing file: " + e);
            e.printStackTrace();
        }
    }
}
