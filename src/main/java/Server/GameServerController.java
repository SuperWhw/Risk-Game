package Server;

import Shared.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;

public class GameServerController {
    private final int PORT;
    private int playerNum;

    public GameServerController(int PORT, int playerNum) throws IOException{
        this.PORT = PORT;
        this.playerNum = playerNum;
    }

    public void run() throws IOException {
        ServerSocket ss = new ServerSocket(PORT);
        try {
            System.out.println("server is running...");
            int currPlayerNum = 0;
            while(currPlayerNum < playerNum) {
                Socket sock = ss.accept();
                System.out.println("connected from " + sock.getRemoteSocketAddress());
                System.out.printf("now we have %d players, waiting other %d players...\n",++currPlayerNum,playerNum-currPlayerNum);
                Thread t = new Handler(sock, playerNum);
                t.start();
            }
        } catch(IOException e) {
            System.out.println("TCP Server creation error");
        }
    }
    public static void main(String[] args) throws IOException{
        GameServerController gameServerController = new GameServerController(6666, 2);
        gameServerController.run();
    }


}

class Handler extends Thread {
    Socket sock;
    int playerNum;

    public Handler(Socket sock, int playerNum) {
        this.sock = sock;
        this.playerNum = playerNum;
    }

    @Override
    public void run() {
        try (InputStream input = this.sock.getInputStream()) {
            try (OutputStream output = this.sock.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            try {
                this.sock.close();
            } catch (IOException ioe) {
                System.out.println("Client close failed!");
            }
            System.out.println("Client disconnected.");
            playerNum--;
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        writer.write("Welcome to the RISK Game!\n" +
                "Please first input your name: ");
        writer.flush();
        for (;;) {
            String s = reader.readLine();
            if (s.equals("bye")) {
                writer.write("bye\n");
                writer.flush();
                break;
            }
            writer.write("ok: " + s + "\n");
            writer.flush();
        }
    }
}