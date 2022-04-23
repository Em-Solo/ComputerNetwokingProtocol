package RecipeExchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server {

    private int serverPort = 42069;
    private DatagramSocket serverSocket = null;

    public Server (int port) {
        if (port < 65536) {
            this.serverPort = port;
        }
    }

    public void start() {
        if (serverSocket != null) return;

        try {

            serverSocket = new DatagramSocket(serverPort);

            System.out.println(
                    "Listening on port: " + serverPort + "!"
            );

            byte[] receiveBuffer = new byte[256];

            while (!serverSocket.isClosed()) {

                try {

                    //problem with incoming clients, who gets received and who ignored
                    DatagramPacket incomingPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(incomingPacket);

                    InetAddress clientAddress = incomingPacket.getAddress();
                    int clientPort = incomingPacket.getPort();

                    String receivedMessage = new String(
                            incomingPacket.getData(),
                            0,
                            incomingPacket.getLength(),
                            StandardCharsets.UTF_8
                    );

                    if (receivedMessage.equalsIgnoreCase("exit")) {
                        serverSocket.close();
                        serverSocket = null;
                        System.out.println("Server listening on port: " + serverPort + " has closed");
                        break;
                    }

                    System.out.println("From Client: " + receivedMessage);

                    //maybe get an input or our recipes
                    String sendbackMessage = "Received your message, meow";
                    byte[] sendbackBuffer = sendbackMessage.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket sendbackPacket = new DatagramPacket(
                            sendbackBuffer,
                            sendbackBuffer.length,
                            clientAddress,
                            clientPort
                    );

                    serverSocket.send(sendbackPacket);

                } catch ( IOException e ) {
                    System.out.println(
                            "Communication error. " +
                                    "Problem with the client?"
                    );
                }

            }

        } catch ( SocketException e ) {

            System.out.println(
                    "Failed on starting the server. " +
                            "Port already taken?"
            );
            e.printStackTrace();
        }
    }
}
