package RecipeExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {

    private int destPort = 42069;
    private String destAddress = null;

    //TODO NEEDS ZERO
    private Integer messageNumber = 0;

    private DatagramSocket clientSocket = null;

    private final int receivingBufferSize = 1000;
    private byte[] receivingBuffer = new byte[this.receivingBufferSize];

    private HelperMethods helperMethods = null;

    private int serverBufferSize = 0;

    public Client(String address, int port) {
        this.helperMethods = new HelperMethods();
        this.destAddress = address;
        if (port < 65536) {
            this.destPort = port;
        }
    }

    public boolean start() {
        boolean reconnect = false;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            System.err.println(
                    "Client: Failed to initialize the client socket. " +
                            "Is there a different free port?"
            );
            e.printStackTrace();
        }

        final InetAddress targetServerAddress;
        try {
            targetServerAddress = InetAddress.getByName(destAddress);
        } catch (UnknownHostException e) {
            System.err.println("Client: Unknown host: " + destAddress);
            e.printStackTrace();
            return reconnect;
        }

        reconnect = this.helloMessage(targetServerAddress);
        if (reconnect) {
            return reconnect;
        }
        this.messageNumber++;

        while (!clientSocket.isClosed()) {
            try {

                String choiceInput = null;
                do {

                    System.out.println("Client: Pick what you would like to do by writing, bellow, the number corresponding to the thing you want to do");
                    System.out.println("1:Request by list, 2:Request by ID, 3:Exit");
                    System.out.print("~>");
                    choiceInput = reader.readLine();

                } while ( !(choiceInput.equals("1") || choiceInput.equals("2") || choiceInput.equals("3")) );


                if (choiceInput.equals("1")) {
                    reconnect = this.listRequest(targetServerAddress);
                    if (reconnect) {
                        return reconnect;
                    }
                    this.messageNumber++;

                }else if (choiceInput.equals("2")) {
                    reconnect = this.idRequest(targetServerAddress);
                    if (reconnect) {
                        return reconnect;
                    }
                    this.messageNumber++;

                } else if (choiceInput.equals("3")) {
                    this.goodbye(targetServerAddress);
                    reconnect = false;
                }


            } catch (IOException e) {
                System.err.println("Client: Error with reading from console");
                e.printStackTrace();
                break;
            }
        }

        try{
            reader.close();
        } catch (IOException e) {
            System.err.println("Client: Reading inputs from client failed, failed attempt to request list, id or goodbye");
            e.printStackTrace();
        }

        return reconnect;

    }

    private boolean helloMessage(InetAddress targetServerAddress) {
        for(int i=1; i<=5; i++) {
            try {
                byte[] header = helperMethods.headerSetup(this.messageNumber, 1, 1, 1);

                int buffSize = receivingBuffer.length;
                byte[] size = helperMethods.intToBytes(buffSize);

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
                receivingBuffer = new byte[this.receivingBufferSize];
                DatagramPacket incomingHelloPacket = new DatagramPacket(
                        receivingBuffer,
                        receivingBuffer.length,
                        targetServerAddress,
                        this.destPort
                );

                try {
                    clientSocket.receive(incomingHelloPacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Client: Timeout, packet was not received after timeout time passed, receiving hello");
                    continue;
                }

                byte[] receivedHelloBuffer = incomingHelloPacket.getData();

                boolean checksumMatches = helperMethods.checkChecksum(receivedHelloBuffer);

                if (checksumMatches) {
                    System.out.println("Client: Checksum matched");

                    if (receivedHelloBuffer[1] == 7){
                        System.out.println("Client: Received error packet from server, hello process, restarting");
                        return this.restart();
                    }

                    byte[] dataOfPacket = Arrays.copyOfRange(receivedHelloBuffer, 8, receivedHelloBuffer.length);
                    this.serverBufferSize = helperMethods.bytesToInt(dataOfPacket);

                    System.out.println("Client: Hello procedure successfully  done");

                    return false;
                } else {
                    System.out.println("Client: Checksum doesnt match");
                    continue;
                }

            } catch (IOException e) {
                System.err.println("Client: Communication error with server at hello step");
                e.printStackTrace();
                continue;
            }
        }

        return this.restart();

    }

    public boolean listRequest(InetAddress serverAddress) {

        System.out.println("Give a name of a recipe you want:");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        while (input == null || input.equals(" ")) {

            try {
                input = reader.readLine();
            } catch (IOException e) {
                System.err.println("Client: Reading from console for a list request, failed");
                e.printStackTrace();
            }

            if (input == null || input.equals(" ")) {
                System.out.println("Client: A null value or a single space is not accepted, re enter a recipe you want");
            }

        }

        for (int i = 1; i<=5; i++) {
            try {

                byte[] header = helperMethods.headerSetup(this.messageNumber, 3, 1, 1);

                byte[] listBuffer = input.getBytes(StandardCharsets.UTF_8);

                byte[] recipeListBuffer = helperMethods.byteArrayConc2(header, listBuffer);

                //calculating and then setting the checksum in the buffer
                recipeListBuffer[4] = helperMethods.checksum(recipeListBuffer).byteValue();

                DatagramPacket recipeSendPacket = new DatagramPacket(
                        recipeListBuffer,
                        recipeListBuffer.length,
                        serverAddress,
                        destPort
                );

                clientSocket.send(recipeSendPacket);

                //emptying buffer size
                receivingBuffer = new byte[this.receivingBufferSize];
                //receiving list of recipes
                DatagramPacket incomingListPacket = new DatagramPacket(
                        receivingBuffer,
                        receivingBuffer.length,
                        serverAddress,
                        destPort
                );


                try {
                    clientSocket.receive(incomingListPacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Client: Timeout, packet was not received after timeout time passed, receiving list");
                    continue;
                }

                byte[] receivedListBuffer = incomingListPacket.getData();

                if (helperMethods.checkChecksum(receivedListBuffer)) {
                    System.out.println("Client: Checksum matched");

                    if (receivedListBuffer[1] == 7) {
                        System.out.println("Client: Received error packet from server, recipe list process, restarting");
                        return this.restart();
                    }

                    String recipeList = null;
                    byte[] dataOfPacket = Arrays.copyOfRange(receivedListBuffer, 8, receivedListBuffer.length);

                    if (dataOfPacket[0] == (byte) 0x0) {

                        System.out.println("Client: You have received an empty list, recipes containing: " + input + " don't exist in the server of the person you request them from");

                    } else {

                        if (receivedListBuffer[2] > 1) {

                            recipeList = this.helperMethods.receiveMultiplePackets(clientSocket, receivingBuffer, incomingListPacket);

                            if (recipeList.equals("error")){
                                return this.restart();
                            }
                            if (recipeList.equals("continue")){
                                continue;
                            }

                        } else {
                            recipeList = new String(
                                    dataOfPacket, 0, helperMethods.indexOf(dataOfPacket, (byte) 0x0), StandardCharsets.UTF_8
                            );
                        }

                        System.out.println("Client: List request successfully  done");
                        System.out.println("Client: The recipe list with their id's, that were received from the server:\n" + recipeList);

                    }

                    return false;

                } else {
                    System.out.println("Client: Checksum doesnt match");
                    continue;
                }

            } catch (IOException e) {
                System.err.println("Client: Communication error with server at recipe list step, either on send or receive");
                e.printStackTrace();
                continue;
            }
        }

        return restart();
    }

    private boolean idRequest(InetAddress serverAddress) {

        System.out.println("Client: Give the ID of the recipe you want:");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        while (input == null || input.equals(" ")) {

            try {
                input = reader.readLine();
            } catch (IOException e) {
                System.err.println("Client: Reading from console for an ID request, failed");
                e.printStackTrace();
            }

            if (input == null || input.equals(" ")) {
                System.out.println("Client: A null value or a single space is not accepted, re enter ID of recipe you want");
            }

        }

        for (int i = 1; i<=5; i++) {
            try {

                byte[] header = helperMethods.headerSetup(this.messageNumber, 2, 1, 1);

                byte[] idBuffer = input.getBytes(StandardCharsets.UTF_8);

                byte[] recipeIdBuffer = helperMethods.byteArrayConc2(header, idBuffer);

                //calculating and then setting the checksum in the buffer
                recipeIdBuffer[4] = helperMethods.checksum(recipeIdBuffer).byteValue();

                DatagramPacket idSendPacket = new DatagramPacket(
                        recipeIdBuffer,
                        recipeIdBuffer.length,
                        serverAddress,
                        destPort
                );

                clientSocket.send(idSendPacket);

                //emptying buffer size
                receivingBuffer = new byte[this.receivingBufferSize];
                //receiving list of recipes
                DatagramPacket incomingRecipePacket = new DatagramPacket(
                        receivingBuffer,
                        receivingBuffer.length,
                        serverAddress,
                        destPort
                );

                try {
                    clientSocket.receive(incomingRecipePacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Client: Timeout, packet was not received after timeout time passed, receiving recipe from id");
                    continue;
                }

                byte[] receivedRecipeBuffer = incomingRecipePacket.getData();

                if (helperMethods.checkChecksum(receivedRecipeBuffer)) {
                    System.out.println("Client: Checksum matched");

                    if (receivedRecipeBuffer[1] == 7){
                        System.out.println("Client: Received error packet from server, recipe ID process, restarting");
                        return this.restart();
                    }

                    byte[] dataOfPacket = Arrays.copyOfRange(receivedRecipeBuffer, 8, receivedRecipeBuffer.length);
                    String recipe = null;

                    if (receivedRecipeBuffer[1] == 8){
                        System.out.println("Client: The recipe you have requested by id was not found in the server of your friend most likely because you used a wrong ID ");
                    } else {

                        if (receivedRecipeBuffer[2] > 1) {

                            recipe = this.helperMethods.receiveMultiplePackets(clientSocket, receivingBuffer, incomingRecipePacket);

                            if (recipe.equals("error")){
                                return this.restart();
                            }
                            if (recipe.equals("continue")){
                                continue;
                            }

                        } else {

                            recipe = new String(
                                    dataOfPacket, 0, helperMethods.indexOf(dataOfPacket, (byte) 0x0), StandardCharsets.UTF_8
                            );

                        }

                        System.out.println("Client: Id request for recipe successfully  done");
                        System.out.println("Client: The recipe you have requested:\n" + recipe);

                    }

                    return false;

                } else {
                    System.out.println("Client: Checksum doesnt match");
                    continue;
                }

            } catch (IOException e) {
                System.err.println("Client: Communication error with server at recipe ID step, either on send or receive");
                e.printStackTrace();
                continue;
            }
        }

        return this.restart();
    }

    private void goodbye(InetAddress serverAddress) {

        try {
            byte[] goodbyeBuff = helperMethods.headerSetup(this.messageNumber, 6, 1, 1);

            //calculating and then setting the checksum in the buffer
            goodbyeBuff[4] = helperMethods.checksum(goodbyeBuff).byteValue();

            DatagramPacket goodbyePacket = new DatagramPacket(
                    goodbyeBuff,
                    goodbyeBuff.length,
                    serverAddress,
                    destPort
            );

            clientSocket.send(goodbyePacket);

            System.out.println("Client: Client sending to address: " + destAddress + " on port: " + destPort + " has closed");

            DatagramPacket incomingRecipePacket = new DatagramPacket(
                    receivingBuffer,
                    receivingBuffer.length,
                    serverAddress,
                    destPort
            );

            try {
                clientSocket.receive(incomingRecipePacket);
            } catch (SocketTimeoutException e) {
                System.out.println("Client: Timeout, packet was not received after timeout time passed, receiving goodbye");
                System.out.println("Client: Client will still close and Server will close after timeout");

                clientSocket.close();
                return;
            }

            System.out.println("Client: Server received the goodbye packet from server, meaning server received goodbye and will also disconnect from client");

        } catch (IOException e) {
            System.err.println("Client: Communication error with server at goodbye step, client still closed , server will just time out");
            e.printStackTrace();
        }

        clientSocket.close();

    }

    private boolean restart() {
        System.out.println("Client: Do you want the client to restart, type yes or no accordingly"
                );

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;

        do {

            try {
                input = reader.readLine();
            } catch (IOException e) {
                System.err.println("Client: Reading from console for a restart confirmation, failed");
                e.printStackTrace();
            }

            if ( !input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no") ) {
                System.out.println("Client: Please type yes or no only");
            }

        } while ( !input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no") );

        if (input.equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

}
