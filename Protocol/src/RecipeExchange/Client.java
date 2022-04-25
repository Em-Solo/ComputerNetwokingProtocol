package RecipeExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private int destPort = 42069;
    private String destAddress = null;

    private DatagramSocket clientSocket = null;

    HelperMethods helperMethods = null;

    private int serverBufferSize = 0;

    public Client(String address, int port) {
        this.helperMethods = new HelperMethods();
        this.destAddress = address;
        if (port < 65536) {
            this.destPort = port;
        }
    }

    private void helloMessage(byte[] receivingBuffer, InetAddress targetServerAddress) {
        for(int i=1; i<=5; i++) {
            try {
                byte[] header = helperMethods.headerSetup(1, 1, 1);

                int buffSize = receivingBuffer.length;
                byte[] size = helperMethods.intToBytes(buffSize);

                //if this kind of byte array concatenation check website for other https://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays
                byte[] helloBuffer = helperMethods.byteArrayConc2(header, size);

                //calculating and then setting the checksum in the buffer
                helloBuffer[4] = helperMethods.checksum(helloBuffer).byteValue();

                DatagramPacket sendPacket = new DatagramPacket(
                        helloBuffer,
                        helloBuffer.length,
                        targetServerAddress,
                        destPort
                );
                clientSocket.send(sendPacket);

                //receiving the hello back from server
                DatagramPacket incomingHelloPacket = new DatagramPacket(
                        receivingBuffer,
                        receivingBuffer.length,
                        targetServerAddress,
                        destPort
                );

                //TODO check if received and repeat if not
                try {
                    clientSocket.receive(incomingHelloPacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout");
                    continue;

                }

                byte[] receivedHelloBuffer = incomingHelloPacket.getData();

                //TODO request another load of the packet if checksum not correct
                boolean chechsumMatches = helperMethods.checkChecksum(receivedHelloBuffer);

                if (chechsumMatches == true) {
                    System.out.println("Checksum matched");

                    byte[] dataOfPacket = Arrays.copyOfRange(receivedHelloBuffer, 8, receivedHelloBuffer.length);
                    serverBufferSize = helperMethods.bytesToInt(dataOfPacket);

                    break;
                } else {
                    System.out.println("Checksum doesnt match");
                }

            } catch (IOException e) {
                System.err.println("Communication error with server at hallo step");
                e.printStackTrace();
            }
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(1000);
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

        this.helloMessage(receivingBuffer, targetServerAddress);

        while (!clientSocket.isClosed()) {
            try {

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
