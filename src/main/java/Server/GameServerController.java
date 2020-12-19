package Server;

import Shared.*;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;

public class GameServerController {

    public static void main(String[] args) throws IOException {
        var server = new BasicTCPServer(6666,2);
        server.CreateSockets();
        server.sendMessage("Hello this is your host\n");
        server.receiveMessage();
        server.print();
        server.end();
    }


}