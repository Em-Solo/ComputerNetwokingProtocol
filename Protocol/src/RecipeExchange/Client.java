package RecipeExchange;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    int destPort = 0;

    public static final String SERVER_HOSTNAME = "localhost";
    DatagramSocket clientSocket = null;

    public Client(int port) {
       this.destPort = port;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        try {
            clientSocket = new DatagramSocket();
        } catch(SocketException ex) {
            System.err.println(
                    "Failed to initialize the client socket. " +
                            "Is there a free port?"
            );
            ex.printStackTrace();
        }

        //check to see if this good or use commented out variable
        final InetAddress targetServerAddress;
        try {
            targetServerAddress = InetAddress.getByName(SERVER_HOSTNAME);
        } catch ( UnknownHostException e ) {
            System.err.println("Unknown host: " + SERVER_HOSTNAME);
            e.printStackTrace();
            return;
        }

        System.out.print("~> ");

        //buffer size subject to change
        byte[] receivingBuffer = new byte[256];

        // loop while user not enters "bye"
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

                    System.out.println("Server: " + responseMessage);

                    System.out.print("> ");

                }

            } catch (IOException e) {
                System.err.println( "Communication error with server" );
                e.printStackTrace();
                break;
            }
        }
    }

//    public static void main(String[] args) {
//        Client client = new Client();
//        client.start();
//    }
}
