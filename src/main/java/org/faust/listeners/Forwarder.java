package org.faust.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Forwarder implements Runnable {

    private final Socket inSocket;
    private final Socket outSocket;
    private final InputStream inInputStream;
    private final OutputStream inOutputStream;
    private final InputStream outInputStream;
    private final OutputStream outOutputStream;

    public Forwarder(Socket inSocket, Socket outSocket) throws IOException {
        this.inSocket = inSocket;
        this.outSocket = outSocket;
        inInputStream = inSocket.getInputStream();
        inOutputStream = inSocket.getOutputStream();

        outInputStream = outSocket.getInputStream();
        outOutputStream = outSocket.getOutputStream();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + ": Starting forwarding.");
        while (true) {
            try {
                sendBetweenStreams(inInputStream, outOutputStream);
                sendBetweenStreams(outInputStream, inOutputStream);
            } catch (IOException e) {
                System.err.println(Thread.currentThread().getId() + ": IOException thrown: " + e);
                return;
            }
        }
    }

    private void sendBetweenStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        int available = inputStream.available();
        if (available > 0) {
            System.out.println("Sending " + available + " bytes.");
            byte[] bytes = new byte[available];
            inputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();
        }
    }
}
