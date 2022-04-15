package org.faust.file;

import org.faust.file.token.HeaderToken;
import org.faust.file.token.Token;

import java.io.*;

public class WSFileWriter {

    private final String filePath;
    private File outputFile;
    private OutputStream outputStream;

    public WSFileWriter(String filePath) throws FileNotFoundException {
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

    private void saveTokenToFile(Token token) {
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

}
