package RecipeExchange;

import java.io.IOException;

public class Entity {
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
