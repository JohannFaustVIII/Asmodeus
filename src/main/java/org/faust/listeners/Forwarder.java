package org.faust.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Forwarder implements Runnable {

    private final InputStream inInputStream;
    private final OutputStream inOutputStream;
    private final InputStream outInputStream;
    private final OutputStream outOutputStream;

    private boolean isActive = true;

    public Forwarder(Socket inSocket, Socket outSocket) throws IOException {
        inInputStream = inSocket.getInputStream();
        inOutputStream = inSocket.getOutputStream();

        outInputStream = outSocket.getInputStream();
        outOutputStream = outSocket.getOutputStream();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getId() + ": Starting forwarding.");
        try {
            Thread t1 = new Thread(() -> sendBetweenStreams(inInputStream, outOutputStream));
            Thread t2 = new Thread(() -> sendBetweenStreams(outInputStream, inOutputStream));
            t1.start();
            t2.start();
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getId() + ": InterruptedException thrown: " + e);
        }
        System.out.println(Thread.currentThread().getId() + ": Stopped forwarding.");
    }

    private void sendBetweenStreams(InputStream inputStream, OutputStream outputStream) {
        try {
            while (isActive) {
                int firstByte = inputStream.read();
                if (firstByte != -1) {
                    outputStream.write(firstByte);
                    int available = inputStream.available();
                    if (available > 0) {
                        System.out.println("Sending " + available + " bytes.");
                        byte[] bytes = new byte[available];
                        inputStream.read(bytes);
                        outputStream.write(bytes);
                    }
                    outputStream.flush();
                } else {
                    isActive = false;
                }
            }
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getId() + ": IOException thrown: " + e);
            isActive = false;
        }
    }
}
