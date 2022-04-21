package RecipeExchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class P2P {
    public static void main(String[] args) throws IOException {
        startServer();
        startClient();
    }

    public static void startClient() {
        (new Thread() {
            @Override
            public void run() {

            }
        }).start();
    }

    public static void startServer() {
        (new Thread() {
            @Override
            public void run() {

            }
        }).start();
    }
}
