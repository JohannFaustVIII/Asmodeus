package org.faust.forwarding;

import lombok.Builder;
import org.faust.pcap.PcapEventHandler;
import org.faust.statistics.StatisticsService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

@Builder
public class Listener {

    private final int inputPort;
    private final int outputPort;
    private final String outIp;
    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;
    private final List<Forwarder> activeForwarders = new LinkedList<>();

    private Thread listenerThread = null;

    public void startListenerThread() {
        listenerThread = new Thread(() -> {
            try {
                listen(); // TODO: consume IOException inside the method?
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
    }

    public void listen() throws IOException {
        System.out.println("Starting server socket on port " + inputPort);
        ServerSocket serverSocket = new ServerSocket(inputPort);

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept(); // it will pause the thread, interrupt may happen here?

            System.out.println("Connecting to output " + outIp + ":" + outputPort);
            Socket outSocket = new Socket(outIp, outputPort);

            Forwarder forwarder = new Forwarder.ForwarderBuilder() // TODO: to refactor: 1. Forwarder can have passed some fields in its constructor, and some via startForwarding (sockets) to use a single Forwarder
                    .inInputStream(socket.getInputStream())
                    .inOutputStream(socket.getOutputStream())
                    .outInputStream(outSocket.getInputStream())
                    .outOutputStream(outSocket.getOutputStream())
                    .statisticsService(statisticsService)
                    .pcapEventHandler(pcapEventHandler)
                    .outIp(outIp)
                    .inIp(socket.getInetAddress().getHostAddress())
                    .outPort(outSocket.getPort())
                    .inPort(socket.getPort())
                    .build();
            activeForwarders.add(forwarder);
            forwarder.startForwarding(); // it should be registered if alive? to know during draining if we wait for something, or to make force close
        }
    }

    public void drain() {
        // TODO: 1. close Listener thread, 2. Let forwarders know about draining? 3. After draining all, deregister handler?
    }

    public void terminate() {
        // TODO: 1. close Listener thread, 2. Terminate forwarders 3. Deregister handler
    }
}
