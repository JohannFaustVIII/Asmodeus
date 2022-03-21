package listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Forwarder {

    private final int inputPort;
    private final int outputPort;
    private final String outIp;

    public Forwarder(int inputPort, int outputPort, String outIp) {
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.outIp = outIp;
    }

    public void startForwarding() throws IOException {
        System.out.println("Starting server socket");
        ServerSocket serverSocket = new ServerSocket(inputPort);
        Socket socket = serverSocket.accept();

        System.out.println("Connecting to output...");
        Socket outSocket = new Socket(outIp, outputPort);

        InputStream inInputStream = socket.getInputStream();
        OutputStream inOutputStream = socket.getOutputStream();

        InputStream outInputStream = outSocket.getInputStream();
        OutputStream outOutputStream = outSocket.getOutputStream();

        System.out.println("Starting forwarding.");
        while(true) {
            sendBetweenStreams(inInputStream, outOutputStream);
            sendBetweenStreams(outInputStream, inOutputStream);
            }
        }

    private void sendBetweenStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        int available = inputStream.available();
        if (available > 0) {
            System.out.println("Sending " + available + " bytes.");
            byte[] bytes = new byte[available];
            inputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();
        }
    }
}
