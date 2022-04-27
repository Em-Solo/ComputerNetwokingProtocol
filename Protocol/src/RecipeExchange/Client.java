package RecipeExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private int destPort = 42069;
    private String destAddress = null;

    //TODO NEEDS ZERO
    private Integer messageNumber = 0;

    private DatagramSocket clientSocket = null;

    private HelperMethods helperMethods = null;

    private int serverBufferSize = 0;

    public Client(String address, int port) {
        this.helperMethods = new HelperMethods();
        this.destAddress = address;
        if (port < 65536) {
            this.destPort = port;
        }
    }

    public void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            System.err.println(
                    "Failed to initialize the client socket. " +
                            "Is there a different free port?"
            );
            e.printStackTrace();
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
        this.messageNumber++;

        while (!clientSocket.isClosed()) {
            try {

                System.out.println();

//                if (System.in.available() > 0) {
//                    String message = scanner.nextLine();
//
//                    if (message.equalsIgnoreCase("exit")) {
//                        this.goodbye();
//                    }
//
//                    byte[] messageSendBuffer = message.getBytes(StandardCharsets.UTF_8);
//                    DatagramPacket sendPacket = new DatagramPacket(
//                            messageSendBuffer,
//                            messageSendBuffer.length,
//                            targetServerAddress,
//                            destPort
//                    );
//                    clientSocket.send(sendPacket);
//
//                    // Contrary to the TCP example, we attempt to receive the
//                    // message from the server right after we've sent it.
//                    //
//                    // This is done to get a trivial working proof-of-concept,
//                    // however this is hardly robust and isn't really useful
//                    // for anything other than a simple echo server.
//                    //
//                    // You might want to use a Java Thread or asynchronous
//                    // Runnable to accept data from clients simultaneously with
//                    // accepting input from the terminal.
//                    DatagramPacket incomingPacket = new DatagramPacket(
//                            receivingBuffer,
//                            receivingBuffer.length,
//                            targetServerAddress,
//                            destPort
//                    );
//                    clientSocket.receive(incomingPacket);
//
//                    String responseMessage = new String(
//                            incomingPacket.getData(), 0, incomingPacket.getLength(), StandardCharsets.UTF_8
//                    );
//
//                    System.out.println("Client: From Server: " + responseMessage);
//
//                }

            } catch (IOException e) {
                System.err.println("Communication error with server");
                e.printStackTrace();
                break;
            }
        }

        try{
            reader.close();
        } catch (IOException e) {
            System.err.println("Reading inputs from client failed, failed attempt to request recipes");
            e.printStackTrace();
        }


    }

    private void helloMessage(byte[] receivingBuffer, InetAddress targetServerAddress) {
        for(int i=1; i<=5; i++) {
            try {
                byte[] header = helperMethods.headerSetup(this.messageNumber, 1, 1, 1);

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
                        this.destPort
                );

                try {
                    clientSocket.receive(incomingHelloPacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Client: Timeout, packet was not received after timeout time passed");
                    continue;
                }

                byte[] receivedHelloBuffer = incomingHelloPacket.getData();

                boolean checksumMatches = helperMethods.checkChecksum(receivedHelloBuffer);

                if (checksumMatches) {
                    System.out.println("Client: Checksum matched");

                    if (receivedHelloBuffer[1] == 7){
                        System.out.println("Client: Received error packet from server, attempting again");
                        continue;
                    }

                    byte[] dataOfPacket = Arrays.copyOfRange(receivedHelloBuffer, 8, receivedHelloBuffer.length);
                    this.serverBufferSize = helperMethods.bytesToInt(dataOfPacket);

                    System.out.println("Client: Hello procedure successfully  done");

                    break;
                } else {
                    System.out.println("Client: Checksum doesnt match");
                }

            } catch (IOException e) {
                System.err.println("Communication error with server at hello step");
                e.printStackTrace();
                continue;
            }
        }
    }

    public void goodbye() {
        byte[] exitBuff = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket exitPacket = new DatagramPacket(
                exitBuff,
                exitBuff.length,
                targetServerAddress,
                destPort
        );
        clientSocket.send(exitPacket);

        clientSocket.close();
        clientSocket = null;

        System.out.println("Client: Client sending to address: " + destAddress + " on port: " + destPort + " has closed");

        break;
    }
}
