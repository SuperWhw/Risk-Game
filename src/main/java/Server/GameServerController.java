package Server;

import Client.GameMap;
import Client.Order;
import Client.Player;
import Client.Territory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GameServerController {
    private ArrayList<Player> players;
    private ArrayList<Territory> territories;
    private int numOfPlayers;
    private GameMap gameMap;
    private ArrayList<Order> receivedOrders;
    private ServerSocket ss;
    private GameServerController() throws IOException{
        ss = new ServerSocket(6666);
    }
    private void run() {
        try {
            System.out.println("server is running...");
            for (; ; ) {
                Socket sock = ss.accept();
                System.out.println("connected from " + sock.getRemoteSocketAddress());
                Thread t = new Handler(sock);
                t.start();
            }
        }
        catch(IOException e) {
            System.out.println("TCP Server creation error");
        }
    }
    public static void main(String[] args)  throws IOException{
        GameServerController gsc = new GameServerController();
        gsc.run();
    }
}

class Handler extends Thread {
    Socket sock;

    public Handler(Socket sock) {
        this.sock = sock;
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
            }
            System.out.println("client disconnected.");
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        writer.write("hello\n");
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