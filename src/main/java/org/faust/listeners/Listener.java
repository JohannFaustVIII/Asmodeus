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

    private Listener(ListenerBuilder builder) {
        this.inputPort = builder.inputPort;
        this.outputPort = builder.outputPort;
        this.outIp = builder.outIp;
        this.statsService = builder.statsService;
        this.wsService = builder.wsService;
    }

    public void listen() throws IOException {
        System.out.println("Starting server socket on port " + inputPort);
        ServerSocket serverSocket = new ServerSocket(inputPort);

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();

            System.out.println("Connecting to output " + outIp + ":" + outputPort);
            Socket outSocket = new Socket(outIp, outputPort);

            Forwarder forwarder = new Forwarder.ForwarderBuilder()
                    .inInputStream(socket.getInputStream())
                    .inOutputStream(socket.getOutputStream())
                    .outInputStream(outSocket.getInputStream())
                    .outOutputStream(outSocket.getOutputStream())
                    .statsService(statsService)
                    .wsService(wsService)
                    .outIp(outIp)
                    .inIp(socket.getInetAddress().getHostAddress())
                    .outPort(outSocket.getPort())
                    .inPort(socket.getPort())
                    .build();
            forwarder.startForwarding();
        }
    }

    public static class ListenerBuilder {

        private int inputPort;
        private int outputPort;
        private String outIp;
        private StatsService statsService;
        private WSService wsService;

        public ListenerBuilder inputPort(int inputPort) {
            this.inputPort = inputPort;
            return this;
        }

        public ListenerBuilder outputPort(int outputPort) {
            this.outputPort = outputPort;
            return this;
        }

        public ListenerBuilder outIp(String outIp) {
            this.outIp = outIp;
            return this;
        }

        public ListenerBuilder statsService(StatsService statsService) {
            this.statsService = statsService;
            return this;
        }

        public ListenerBuilder wsService(WSService wsService) {
            this.wsService = wsService;
            return this;
        }

        public Listener build() {
            return new Listener(this);
        }
    }
}
