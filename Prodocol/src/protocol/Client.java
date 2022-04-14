package protocol;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static final String SERVER_HOSTNAME = "localhost";

    DatagramSocket clientSocket;

    public static void main(Sting[] args) {
        var client = new Client();
        client.start();
    }

    public void start() {
        var scanner = new Scanner(System.in);

        try{
            clientSocket = new DatagramSocket();
        } catch(SocketException e) {
            System.err.pintln(
                    "Failed to initialize clients socket. " +
                            "Is there a free port?"
            );
            e.printStackTrace();
        }

        final InetAddress serverAddress;
        try{
            serverAddress = InetAddress.getByName(SERVER_HOSTNAME);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_HOSTNAME);
            e.printStackTrace();
            return;
        }

        System.out.print(">>>");

        byte[] buffer = new byte[256];

        while (!clientSocket.isClossed()) {

            try{
                if (System.in.available() > 0) {
                    String message = scanner.nextLine();

                    if (message.equalsIgnoreCase("exit")) {
                        var exitBuffer = message.getBytes(StandardCharacters.UTF_8);
                        clientSocket.send(new DatagramPacket(
                                exitBuffer,
                                exitBuffer.length,
                                serverAddress,
                                RecipeProtocol.PORT;
                        ));

                        clientSocket.close();
                        break;
                                            }
                }
            }

        }
    }

}
