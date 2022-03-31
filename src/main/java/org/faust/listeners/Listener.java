package org.faust.listeners;

import org.faust.stats.StatsService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {

    private final int inputPort;
    private final int outputPort;
    private final String outIp;

    public Listener(int inputPort, int outputPort, String outIp) {
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.outIp = outIp;
    }

    public void startListening() throws IOException {
        System.out.println("Starting server socket on port " + inputPort);
        ServerSocket serverSocket = new ServerSocket(inputPort);
        StatsService service = new StatsService();

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();

            System.out.println("Connecting to output " + outIp + ":" + outputPort);
            Socket outSocket = new Socket(outIp, outputPort);

            Forwarder forwarder = new Forwarder(socket, outSocket, service);
            forwarder.startForwarding();
        }
    }
}
