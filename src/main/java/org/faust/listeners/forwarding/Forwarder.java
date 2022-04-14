package org.faust.listeners.forwarding;

import org.faust.file.WSService;
import org.faust.stats.StatsService;

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
    private final StatsService statsService;
    private final WSService wsService;
    private final String outIp;
    private final String inIp;

    public Forwarder(Socket inSocket, Socket outSocket, StatsService statsService, WSService wsService, String outIp) throws IOException {
        inInputStream = inSocket.getInputStream();
        inOutputStream = inSocket.getOutputStream();

        outInputStream = outSocket.getInputStream();
        outOutputStream = outSocket.getOutputStream();

        this.statsService = statsService;
        this.wsService = wsService;
        this.outIp = outIp;
        this.inIp = inSocket.getInetAddress().getHostAddress();
    }

    public void startForwarding() {
        Phaser phaser = new Phaser();
        Thread t1 = new Thread(new ForwardingStream(phaser, inInputStream, outOutputStream, statsService, wsService, inIp, outIp));
        Thread t2 = new Thread(new ForwardingStream(phaser, outInputStream, inOutputStream, statsService, wsService, outIp, inIp));
        t1.start();
        t2.start();
    }
}
