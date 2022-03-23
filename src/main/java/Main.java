import config.EnvironmentService;
import listeners.Forwarder;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        EnvironmentService envService = new EnvironmentService();
        int inputPort = envService.getInputPort();
        int outputPort = envService.getOutputPort();
        String outIP = envService.getOutIP();

        Forwarder forwarder = new Forwarder(inputPort, outputPort, outIP);
        forwarder.startForwarding();
    }

}
