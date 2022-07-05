package org.faust.forwarding;

import org.faust.pcap.PcapEventHandler;
import org.faust.statistics.StatisticsService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {

    private final int inputPort;
    private final int outputPort;
    private final String outIp;
    private final StatisticsService statisticsService;
    private final PcapEventHandler pcapEventHandler;

    private Listener(ListenerBuilder builder) {
        this.inputPort = builder.inputPort;
        this.outputPort = builder.outputPort;
        this.outIp = builder.outIp;
        this.statisticsService = builder.statisticsService;
        this.pcapEventHandler = builder.pcapEventHandler; //TODO: think about removing/cleaning the handler?
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
                    .statsService(statisticsService)
                    .wsEventHandler(pcapEventHandler)
                    .outIp(outIp)
                    .inIp(socket.getInetAddress().getHostAddress())
                    .outPort(outSocket.getPort())
                    .inPort(socket.getPort())
                    .build();
            forwarder.startForwarding(); // it should be registered if alive? to know during draining if we wait for something, or to make force close
        }
    }

    public static class ListenerBuilder {

        private int inputPort;
        private int outputPort;
        private String outIp;
        private StatisticsService statisticsService;
        private PcapEventHandler pcapEventHandler;

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

        public ListenerBuilder statsService(StatisticsService statisticsService) {
            this.statisticsService = statisticsService;
            return this;
        }

        public ListenerBuilder wsHandler(PcapEventHandler pcapEventHandler) {
            this.pcapEventHandler = pcapEventHandler;
            return this;
        }

        public Listener build() {
            return new Listener(this);
        }
    }
}
