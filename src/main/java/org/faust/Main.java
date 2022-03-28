package org.faust;

import org.faust.config.EnvironmentService;
import org.faust.listeners.Listener;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        EnvironmentService envService = new EnvironmentService();
        int inputPort = envService.getInputPort();
        int outputPort = envService.getOutputPort();
        String outIP = envService.getOutIP();

        Listener listener = new Listener(inputPort, outputPort, outIP);
        listener.startListening();
    }

}
