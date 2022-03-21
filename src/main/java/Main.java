import listeners.Forwarder;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int inputPort = getInputPort();
        int outputPort = getOutputPort();
        String outIP = getOutIP();

        Forwarder forwarder = new Forwarder(inputPort, outputPort, outIP);
        forwarder.startForwarding();
    }

    private static int getInputPort() {
        String inPort = System.getenv("ASMO_IN_PORT");
        if (inPort == null) {
            return 8012;
        } else {
            return Integer.parseInt(inPort);
        }
    }

    private static int getOutputPort() {
        String outPort = System.getenv("ASMO_OUT_PORT");
        if (outPort == null) {
            return 5432;
        } else {
            return Integer.parseInt(outPort);
        }
    }

    private static String getOutIP() {
        String outIP = System.getenv("ASMO_OUT_IP");

        if (outIP == null) {
            outIP = "127.0.0.1";
        }
        return outIP;
    }

}
