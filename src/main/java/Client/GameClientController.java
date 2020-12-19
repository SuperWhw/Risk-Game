package Client;

import Server.BasicTCPServer;
import Shared.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClientController {

    public static void main(String[] args) throws IOException {

        var client = new BasicTCPClient(6666, "127.0.0.1", "localhost");
        client.BuildConnection();
        System.out.println(client.ReceiveMessage());
        System.out.print(">>> "); // 打印提示
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine(); // 读取一行输入
        client.SendMessage(s + "\n");
        client.end();

    }
}