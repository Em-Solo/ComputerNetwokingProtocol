package RecipeExchange;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {

    private int serverPort = 42069;
    private DatagramSocket serverSocket = null;

    private final int receivingBufferSize = 1000;
    byte[] receivingBuffer = new byte[receivingBufferSize];

    private HelperMethods helperMethods = null;

    private Recipes recipes = null;

    private InetAddress clientAddress = null;
    private int clientPort = 0;
    private boolean connected = false;

    private int clientBufferSize = 0;

    public Server (int port) {
        this.helperMethods =new HelperMethods();
        this.recipes = new Recipes();
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

            while (!serverSocket.isClosed()) {

                try {

                    this.receivingBuffer = new byte[this.receivingBufferSize];
                    DatagramPacket incomingPacket = new DatagramPacket(
                            this.receivingBuffer,
                            this.receivingBuffer.length);


                    try {
                        this.serverSocket.receive(incomingPacket);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Server: Timeout, Server has been idle on receive for 5 minutes");

                        this.serverReset();

                        continue;
                    }

                    byte[] receivedBufferFromClient = incomingPacket.getData();

                    if (!helperMethods.checkChecksum(receivedBufferFromClient)){
                        helperMethods.errorPacketSend(serverSocket, incomingPacket);

                        this.serverReset();

                        continue;
                    }

                    if (!incomingPacket.getAddress().equals(this.clientAddress)) {
                        if (!connected && receivedBufferFromClient[1] == 1) {
                            connected = true;
                            this.clientAddress = incomingPacket.getAddress();
                            this.clientPort = incomingPacket.getPort();
                        } else {
                            System.out.println("Server: Someone is trying to connect to you, but we send error package because you are either already busy with a another client, or a new client tries to connect with wrong initial packet");
                            helperMethods.errorPacketSend(serverSocket, incomingPacket);
                            continue;
                        }
                    }



                    byte packetType = receivedBufferFromClient[1];

                    switch (packetType) {
                        case 0:
                            System.out.println("random aknoladgment received");
                            break;
                        case 1:
                            System.out.println("starting hello preccess");
                            this.helloResponseMessage(receivedBufferFromClient);
                            System.out.println("exiting hello phase");
                            break;
                        case 2:
                            System.out.println("entering sending recipe from id");
                            this.recipeResponseFromId(receivedBufferFromClient);
                            System.out.println("recipe from id was sent");
                            break;
                        case 3:
                            System.out.println("Entering sending list of recipes");
                            this.listOfRecipesResponse(receivedBufferFromClient);
                            System.out.println("list of recipes sent");
                            break;
                        case 6:
                            System.out.println("entering goodbye phase");
                            this.goodbye(receivedBufferFromClient);
                            System.out.println("leaving goodbye phase");
                            break;
                        default:
                            helperMethods.errorPacketSend(serverSocket, incomingPacket);
                            this.serverReset();
                            continue;
                    }

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
                            "Port might be taken"
            );
            e.printStackTrace();
        }
    }

    private void helloResponseMessage(byte[] helloBufferClient) {

        //processing the data of the hallo packet
        byte[] dataOfPacket = Arrays.copyOfRange(helloBufferClient, 8, helloBufferClient.length);
        this.clientBufferSize = helperMethods.bytesToInt(dataOfPacket);

        //sending back hallo message
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
            System.err.println("Server: Communication error with client at hello step");
            e.printStackTrace();
        }
    }

    private void listOfRecipesResponse(byte[] listRequestBufferClient) {

        byte[] dataOfPacket = Arrays.copyOfRange(listRequestBufferClient, 8, listRequestBufferClient.length);
        String recipeName = new String(
                dataOfPacket, 0, helperMethods.indexOf(dataOfPacket, (byte) 0x0), StandardCharsets.UTF_8
        );

        byte[] recipeList = this.recipes.listRepresentationOfSpecificRecipes(recipeName).getBytes(StandardCharsets.UTF_8);

        Byte messageNumber = listRequestBufferClient[0];


        if ( (recipeList.length +  8) > this.clientBufferSize) {

            boolean sent = this.helperMethods.sendMultiplePackets(this.serverSocket, this.receivingBuffer, this.clientAddress, clientPort, recipeList, this.clientBufferSize, messageNumber.intValue(), 4 );

            if (!sent) {
                this.serverReset();
                return;
            }

        } else {

            try {

                byte[] header = helperMethods.headerSetup(messageNumber.intValue(), 4, 1, 1);


                byte[] recipeListBuffer = helperMethods.byteArrayConc2(header, recipeList);


                //calculating and then setting the checksum in the buffer
                recipeListBuffer[4] = helperMethods.checksum(recipeListBuffer).byteValue();

                DatagramPacket sendPacket = new DatagramPacket(
                        recipeListBuffer,
                        recipeListBuffer.length,
                        clientAddress,
                        clientPort
                );
                serverSocket.send(sendPacket);

            } catch (IOException e) {
                System.err.println("Server: Communication error with client at recipe list request step");
                e.printStackTrace();
            }

        }

        System.out.println("Server: List of recipes containing the string the client send to the server, has been sent back to the client, will be empty if no such recipes exist");

    }

    private void recipeResponseFromId(byte[] idRequestBufferClient) {

        byte[] dataOfPacket = Arrays.copyOfRange(idRequestBufferClient, 8, idRequestBufferClient.length);
        String recipeId = new String(
                dataOfPacket, 0, helperMethods.indexOf(dataOfPacket, (byte) 0x0), StandardCharsets.UTF_8
        );

        try{
            Byte messageNumber = idRequestBufferClient[0];
            byte[] header = null;

            String recipeString = this.recipes.recipeFromId(recipeId);


            byte[] recipeFromIdBuffer = null;

            if (recipeString == null) {

                recipeFromIdBuffer = helperMethods.headerSetup(messageNumber.intValue(), 8, 1, 1);

                //calculating and then setting the checksum in the buffer
                recipeFromIdBuffer[4] = helperMethods.checksum(recipeFromIdBuffer).byteValue();

                DatagramPacket sendPacket = new DatagramPacket(
                        recipeFromIdBuffer,
                        recipeFromIdBuffer.length,
                        clientAddress,
                        clientPort
                );
                serverSocket.send(sendPacket);

                System.out.println("Server: The recipe requested by Id was not found, so a Not Found package was send");

            } else {

                byte[] recipeFromId = recipeString.getBytes(StandardCharsets.UTF_8);

                if ( (recipeFromId.length + 8) > clientBufferSize) {

                    boolean sent = this.helperMethods.sendMultiplePackets(this.serverSocket, this.receivingBuffer, this.clientAddress, clientPort, recipeFromId, this.clientBufferSize, messageNumber.intValue(), 5);

                    if (!sent) {
                        this.serverReset();
                        return;
                    }

                } else {

                    header = helperMethods.headerSetup(messageNumber.intValue(), 5, 1, 1);


                    recipeFromIdBuffer = helperMethods.byteArrayConc2(header, recipeFromId);

                    //calculating and then setting the checksum in the buffer
                    recipeFromIdBuffer[4] = helperMethods.checksum(recipeFromIdBuffer).byteValue();

                    DatagramPacket sendPacket = new DatagramPacket(
                            recipeFromIdBuffer,
                            recipeFromIdBuffer.length,
                            clientAddress,
                            clientPort
                    );
                    serverSocket.send(sendPacket);

                }

            }

        } catch (IOException e) {
            System.err.println("Server: Communication error with client at recipe ID request step");
            e.printStackTrace();
        }

        System.out.println("Server: The recipe requested by Id has been sent to the client");

    }

    private void goodbye(byte[] goodbyeBufferClient) {

        System.out.println("Server: The server recieved a goodbye from the client so it will try to send one back and the server will be reset");

        try{
            Byte messageNumber = goodbyeBufferClient[0];
            byte[] goodbyeBuffer = helperMethods.headerSetup(messageNumber.intValue(), 6, 1, 1);

            //calculating and then setting the checksum in the buffer
            goodbyeBuffer[4] = helperMethods.checksum(goodbyeBuffer).byteValue();

            DatagramPacket sendPacket = new DatagramPacket(
                    goodbyeBuffer,
                    goodbyeBuffer.length,
                    clientAddress,
                    clientPort
            );
            serverSocket.send(sendPacket);

        } catch (IOException e) {
            System.err.println("Server: Communication error with client at goodbye step");
            e.printStackTrace();
        }

        this.serverReset();

    }

    private void serverReset() {
        clientAddress = null;
        clientPort = 0;
        connected = false;

        receivingBuffer = new byte[receivingBufferSize];

        clientBufferSize = 0;


    }

}
