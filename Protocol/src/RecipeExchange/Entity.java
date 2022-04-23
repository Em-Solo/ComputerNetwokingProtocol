package RecipeExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Entity {

    private Client client = null;
    private Server server = null;

    public void startClient(String address, int port) {
        (new Thread() {
            @Override
            public void run() {
                client = new Client(address, port);
                client.
                        start();
            }
        }).start();
    }

    public void startServer(int port) {
        (new Thread() {
            @Override
            public void run() {
                server = new Server(port);
                server.start();
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Entity entity = new Entity();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int clientPort = 65536, serverPort = 65536;
        String ip = null;
        int tmpPort;
        String message = null;

        //block of code for server port input.
        System.out.println("Give a port for the Server if you don't want to use the default, otherwise press enter");
        System.out.print("~> ");
        message = reader.readLine();
        if (!message.isBlank()) {
            tmpPort = Integer.parseInt(message);
            if (tmpPort < 65536) {
                serverPort = tmpPort;
            } else {
                System.out.println("Default port 42069 will be used");
            }
        } else {
            System.out.println("Default port 42069 will be used");
        }

        message = null;

        //block of code for client address input.
        System.out.println("Give the address you want the client to connect to");
        boolean gotAddress = false;
        while (!gotAddress) {
            System.out.print("~> ");
            message = reader.readLine();
            try {
                InetAddress.getByName(message);
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + message);
                System.out.println("Give a different address: ");
                continue;
            }
            ip = message;
            if (ip.isBlank()) {
                System.out.println("Give a different address, an empty address is not acceptable: ");
            } else {
                gotAddress = true;
            }
        }

        message = null;

        //block of code for client port input.
        System.out.println("Give a port for the Client otherwise press enter for default port to be used");
        System.out.print("~> ");
        message = reader.readLine();
        if (!message.isBlank()) {
            tmpPort = Integer.parseInt(message);
            if (tmpPort < 65536) {
                clientPort = tmpPort;
            } else {
                System.out.println("Default port 42069 will be used");
            }
        } else {
            System.out.println("Default port 42069 will be used");
        }

        reader.close();

        entity.startClient(ip, clientPort);
        entity.startServer(serverPort);
    }

}
