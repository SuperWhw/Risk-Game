package Server;

import Client.GameClientViewer;
import Shared.*;
import Utilities.FileIOBasics;
import Utilities.GameJsonUtils;

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
        var server = new BasicTCPServer(6666,3);
        server.CreateSockets();
        server.sendMessage("Hello this is your host, please input your name:\n");
        var players = server.receiveMessage();
        server.end();

        var playerList = new ArrayList<Player>();
        for(var player : players) {
            playerList.add(new Player(player));
        }

        var fileIO = new FileIOBasics();
        var MapStr = fileIO.readJsonFile("map.json");

        var jsonUtil = new GameJsonUtils();
        var gameMap = jsonUtil.readJsonToGameMap(MapStr, players);

        var viewer = new GameClientViewer();
        for(var player : gameMap.getPlayerMap().values()) {
            viewer.printMap(gameMap, player, "order");
        }

        var MapStrAfterOrders = jsonUtil.writeMapToJson(gameMap,null);
        System.out.println(MapStrAfterOrders);


    }


}