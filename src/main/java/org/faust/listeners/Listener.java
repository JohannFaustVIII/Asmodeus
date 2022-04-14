package org.faust.listeners;

import org.faust.file.WSService;
import org.faust.listeners.forwarding.Forwarder;
import org.faust.stats.StatsService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {

    private final int inputPort;
    private final int outputPort;
    private final String outIp;
    private final StatsService statsService;
    private final WSService wsService;

    public Listener(int inputPort, int outputPort, String outIp, StatsService statsService, WSService wsService) {
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.outIp = outIp;
        this.statsService = statsService;
        this.wsService = wsService;
    }

    public void listen() throws IOException {
        System.out.println("Starting server socket on port " + inputPort);
        ServerSocket serverSocket = new ServerSocket(inputPort);

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();

            System.out.println("Connecting to output " + outIp + ":" + outputPort);
            Socket outSocket = new Socket(outIp, outputPort);

            Forwarder forwarder = new Forwarder(socket, outSocket, statsService, wsService, outIp);
            forwarder.startForwarding();
        }
    }
}
