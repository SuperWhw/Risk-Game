package Server;

import Client.GameClientViewer;
import Shared.*;
import Utilities.FileIOBasics;
import Utilities.GameJsonUtils;

import Utilities.*;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;

public class GameServerController {
    BasicTCPServer server;
    ColorPrint printer;
    GameMap gameMap;
    FileIOBasics fileIO;
    GameJsonUtils jsonUtils;
    GameClientViewer viewer;
    GameServerController(int port, int playerNum) {
        printer = new ColorPrint();
        fileIO = new FileIOBasics();
        jsonUtils = new GameJsonUtils();
        viewer = new GameClientViewer();
        try {
            server = new BasicTCPServer(port, playerNum);
        }
        catch (IOException e) {
            printer.printWithColor("TCP Server build failure, please check your network",
                    ColorPrint.Color.RED);
        }
    }

    public boolean isGameDone(){
        for(var player : gameMap.getPlayerMap().values()) {
            if(player.getTerritories().size() == gameMap.getTerritoryMap().size()) {
                return true;
            }
        }
        return false;
    }

    void InitializeMap() {
        server.sendMessage("Hello this is your host, please input your name:\n");

        // create n players
        var players = server.receiveMessage();
        var playerList = new ArrayList<Player>();
        for(var player : players) {
            playerList.add(new Player(player));
        }

        // read and load map.json
        var MapStr = fileIO.readJsonFile("map.json");
        gameMap = jsonUtils.readJsonToGameMap(MapStr, players);

        // printMap

        var viewer = new GameClientViewer();
        for(var player : gameMap.getPlayerMap().values()) {
            viewer.printMap(gameMap, player, "order");
        }

    }
    public static void main(String[] args) throws IOException {
        var control = new GameServerController(6666,3);
        control.InitializeMap();

    }


}