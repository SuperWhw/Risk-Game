package Server;

import java.io.IOException;

public class TestServer {
    public static void main(String[] args) throws IOException {
        int playerNum = 3;
        GameServerController gsc = new GameServerController(6666,playerNum);
        gsc.run();
    }
}
