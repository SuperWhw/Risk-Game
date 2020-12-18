package Client;

import Shared.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClientController {
    private Player player;
    private int numOfPlayers;
    private GameMap gameMap;
    private ArrayList<Territory> Territories;
    private Socket sock;
    private GameClientController() throws java.net.UnknownHostException, IOException{
        this.sock = new Socket("localhost",6666);
    }
    public void buildConnection() throws IOException {
        try (InputStream input = sock.getInputStream()) {
            try (OutputStream output = sock.getOutputStream()) {
                handle(input, output);
            }
        } catch(IOException e) {
            System.out.println("TCP Client creation error");
        }
        this.sock.close();
        System.out.println("disconnected.");
    }

    private static void handle(InputStream input, OutputStream output) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);
        System.out.println("[server] " + reader.readLine());
        for (;;) {
            System.out.print(">>> "); // 打印提示
            String s = scanner.nextLine(); // 读取一行输入
            writer.write(s);
            writer.newLine();
            writer.flush();
            String resp = reader.readLine();
            System.out.println("<<< " + resp);
            if (resp.equals("bye")) {
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GameClientController ccc = new GameClientController();
        ccc.buildConnection();
    }
}