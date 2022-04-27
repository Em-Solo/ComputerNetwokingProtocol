package RecipeExchange;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {

    private int serverPort = 42069;
    private DatagramSocket serverSocket = null;

    private HelperMethods helperMethods = null;

    private InetAddress clientAddress = null;
    private int clientPort = 0;
    private boolean connected = false;
    //TODO remember to empty them out when you close the connection

    private int clientBufferSize = 0;

    public Server (int port) {
        this.helperMethods =new HelperMethods();
        if (port < 65536) {
            this.serverPort = port;
        }
    }

    public void start() {
        if (serverSocket != null) return;

        try {

            serverSocket = new DatagramSocket(serverPort);
            //making it so server doesnt wait forever
            serverSocket.setSoTimeout(300000);

            System.out.println(
                    "Server: Listening on port: " + serverPort + "!"
            );

            byte[] receivingBuffer = new byte[256];

            while (!serverSocket.isClosed()) {

                try {


                    DatagramPacket incomingPacket = new DatagramPacket(
                            receivingBuffer,
                            receivingBuffer.length);


                    try {
                        serverSocket.receive(incomingPacket);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Server: Timeout, Server has been idle on receive for 5 minutes");
                        return;
                    }

                    byte[] receivedBufferFromClient = incomingPacket.getData();

                    if (!helperMethods.checkChecksum(receivedBufferFromClient)){
                        helperMethods.errorPacketSend(serverSocket, incomingPacket);
                    }

                    if (incomingPacket.getAddress() != this.clientAddress  ) {
                        if (!connected && receivedBufferFromClient[1] == 1) {
                            connected = true;
                            this.clientAddress = incomingPacket.getAddress();
                            this.clientPort = incomingPacket.getPort();
                        } else {
                            helperMethods.errorPacketSend(serverSocket, incomingPacket);
                        }
                    }



                    byte packetType = receivedBufferFromClient[1];

                    switch (packetType) {
                        case 0:
                            break;
                        case 1:
                            this.helloResponseMessage(receivingBuffer, receivedBufferFromClient);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        default:
                            helperMethods.errorPacketSend(serverSocket, incomingPacket);
                    }
//                    String receivedMessage = new String(
//                            incomingPacket.getData(),
//                            0,
//                            incomingPacket.getLength(),
//                            StandardCharsets.UTF_8
//                    );
//
//                    if (receivedMessage.equalsIgnoreCase("exit")) {
//                        serverSocket.close();
//                        serverSocket = null;
//                        System.out.println("Server listening on port: " + serverPort + " has closed");
//                        break;
//                    }
//
//                    System.out.println("Server: From Client: " + receivedMessage);
//
//                    //maybe get an input or our recipes
//                    String sendbackMessage = "Received your message, meow";
//                    byte[] sendbackBuffer = sendbackMessage.getBytes(StandardCharsets.UTF_8);
//
//                    DatagramPacket sendbackPacket = new DatagramPacket(
//                            sendbackBuffer,
//                            sendbackBuffer.length,
//                            clientAddress,
//                            clientPort
//                    );
//
//                    serverSocket.send(sendbackPacket);

                } catch ( IOException e ) {
                    System.out.println(
                            "Server: Communication error. " +
                                    "Problem with the client?"
                    );
                }

            }

        } catch ( SocketException e ) {

            System.out.println(
                    "Server: Failed on starting the server. " +
                            "Port already taken?"
            );
            e.printStackTrace();
        }
    }

    private void helloResponseMessage(byte[] receivingBuffer, byte[] helloBufferClient) {

        byte[] dataOfPacket = Arrays.copyOfRange(helloBufferClient, 8, helloBufferClient.length);
        this.clientBufferSize = helperMethods.bytesToInt(dataOfPacket);

        try{
            Byte messageNumber = helloBufferClient[0];
            byte[] header = helperMethods.headerSetup(messageNumber.intValue(), 1, 1, 1);

            int buffSize = receivingBuffer.length;
            byte[] size = helperMethods.intToBytes(buffSize);

            byte[] helloBuffer = helperMethods.byteArrayConc2(header, size);

            //calculating and then setting the checksum in the buffer
            helloBuffer[4] = helperMethods.checksum(helloBuffer).byteValue();

            DatagramPacket sendPacket = new DatagramPacket(
                    helloBuffer,
                    helloBuffer.length,
                    clientAddress,
                    clientPort
            );
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            System.err.println("Communication error with client at hello step");
            e.printStackTrace();
        }
    }


}
