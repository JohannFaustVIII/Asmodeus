package org.faust.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Phaser;

public class Forwarder {

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

    public void startForwarding() {
        Phaser phaser = new Phaser();
        Thread t1 = new Thread(new ForwardingStream(phaser, inInputStream, outOutputStream));
        Thread t2 = new Thread(new ForwardingStream(phaser, outInputStream, inOutputStream));
        t1.start();
        t2.start();
    }
}
