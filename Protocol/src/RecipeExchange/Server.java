package RecipeExchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server {

    DatagramSocket serverSocket = null;

    public void start() {

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
