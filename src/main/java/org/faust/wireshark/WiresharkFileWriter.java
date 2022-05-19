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
        try {
            outputStream.write(token.toBytes());
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Exception during writing token to file: " + e);
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

}
