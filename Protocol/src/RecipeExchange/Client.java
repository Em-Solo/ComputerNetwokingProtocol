package RecipeExchange;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private int destPort = 42069;
    private String destAddress = null;

    private DatagramSocket clientSocket = null;

    public Client(String address, int port) {
        this.destAddress = address;
        if (port < 65536) {
            this.destPort = port;
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            System.err.println(
                    "Failed to initialize the client socket. " +
                            "Is there a different free port?"
            );
            ex.printStackTrace();
        }

        final InetAddress targetServerAddress;
        try {
            targetServerAddress = InetAddress.getByName(destAddress);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + destAddress);
            e.printStackTrace();
            return;
        }

        //buffer size subject to change
        byte[] receivingBuffer = new byte[256];

        while (!clientSocket.isClosed()) {
            try {
                // TODO: when should a user be able to send messages?
                //  Should every outgoing message expect a response before
                //  another message can be sent?

                if (System.in.available() > 0) {
                    String message = scanner.nextLine();

                    if (message.equalsIgnoreCase("exit")) {
                        byte[] exitBuff = message.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket exitPacket = new DatagramPacket(
                                exitBuff,
                                exitBuff.length,
                                targetServerAddress,
                                destPort
                        );
                        clientSocket.send(exitPacket);

                        clientSocket.close();

                        System.out.println("Client sending to address: " + destAddress + " on port: " + destPort + " has closed");

                        break;
                    }

                    byte[] messageSendBuffer = message.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket sendPacket = new DatagramPacket(
                            messageSendBuffer,
                            messageSendBuffer.length,
                            targetServerAddress,
                            destPort
                    );
                    clientSocket.send(sendPacket);

                    // Contrary to the TCP example, we attempt to receive the
                    // message from the server right after we've sent it.
                    //
                    // This is done to get a trivial working proof-of-concept,
                    // however this is hardly robust and isn't really useful
                    // for anything other than a simple echo server.
                    //
                    // You might want to use a Java Thread or asynchronous
                    // Runnable to accept data from clients simultaneously with
                    // accepting input from the terminal.
                    DatagramPacket incomingPacket = new DatagramPacket(
                            receivingBuffer,
                            receivingBuffer.length,
                            targetServerAddress,
                            destPort
                    );
                    clientSocket.receive(incomingPacket);

                    String responseMessage = new String(
                            incomingPacket.getData(), 0, incomingPacket.getLength(), StandardCharsets.UTF_8
                    );

                    System.out.println("From Server: " + responseMessage);

                }

            } catch (IOException e) {
                System.err.println("Communication error with server");
                e.printStackTrace();
                break;
            }
        }
    }
}
