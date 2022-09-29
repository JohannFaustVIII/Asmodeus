package org.faust.forwarding;

import lombok.Builder;
import org.faust.pcap.PcapEventHandler;
import org.faust.statistics.StatisticsService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Listener {

    private final int inputPort;
    private final int outputPort;
    private final String outIp;
    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;
    private Forwarder activeForwarder;

    private final Object threadLock = new Object();
    private Thread listenerThread = null;

    @Builder
    public Listener(int inputPort, int outputPort, String outIp, StatisticsService statisticsService, PcapEventHandler pcapEventHandler) {
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.outIp = outIp;
        this.statisticsService = statisticsService;
        this.pcapEventHandler = pcapEventHandler;
    }

    public void startListenerThread() {
        if (isListenerThreadRunning()) {
            return;
        }

        synchronized (threadLock) {
            if (isListenerThreadRunning()) {
                return;
            }

            listenerThread = new Thread(this::listen);
            listenerThread.start();
        }
    }

    private boolean isListenerThreadRunning() {
        // TODO: is it enough to check?
        return listenerThread != null && listenerThread.getState() != Thread.State.NEW && listenerThread.getState() != Thread.State.TERMINATED;
    }

    public void listen() {
        System.out.println("Starting server socket on port " + inputPort);
        ServerSocket serverSocket = getServerSocket();

        activeForwarder = new Forwarder.ForwarderBuilder()
                .pcapEventHandler(pcapEventHandler)
                .statisticsService(statisticsService)
                .build(); // TODO: move to constructor??? to remove from builder

        while (!serverSocket.isClosed()) {
            Socket socket = listenSocket(serverSocket);

            System.out.println("Connecting to output " + outIp + ":" + outputPort);
            Socket outSocket = openClientSocket();

            activeForwarder.startForwarding(socket, outSocket);
        }
    }

    private ServerSocket getServerSocket() {
        try {
            return new ServerSocket(inputPort);
        } catch (IOException e) {
            System.err.println("IOException was thrown for ServerSocket locking. Port: " + inputPort + " may be not available.");
            throw new RuntimeException(e);
        }
    }

    private Socket listenSocket(ServerSocket serverSocket) {
        try {
            return serverSocket.accept(); // it will pause the thread, interrupt may happen here?
        } catch (IOException e) {
            System.err.println("IO exception during listening the socket: " + e);
            e.printStackTrace();
            // TODO: probably, the thread should be closed in this place
            throw new RuntimeException(e);
        }
    }

    private Socket openClientSocket() {
        try {
            return new Socket(outIp, outputPort);
        } catch (UnknownHostException e) {
            System.err.println("Exception during determining IP address of the host: " + outIp);
            e.printStackTrace();
            throw new RuntimeException(e); // TODO: still to refactor?
        } catch (IOException e) {
            System.err.println("IO exception during opening the socket: " + e);
            e.printStackTrace();
            throw new RuntimeException(e); // TODO: still to refactor + add handling of interrupt
        }
    }

    public void drain() {
        // TODO: 1. close Listener thread, 2. Let forwarders know about draining? 3. After draining all, deregister handler?
    }

    public void terminate() {
        // TODO: 1. close Listener thread, 2. Terminate forwarders 3. Deregister handler
    }
}
