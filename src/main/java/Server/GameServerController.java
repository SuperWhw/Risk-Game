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
    OrderHandler handler;
    GameServerController(int port, int playerNum) {
        printer = new ColorPrint();
        fileIO = new FileIOBasics();
        jsonUtils = new GameJsonUtils();
        handler = new OrderHandler();
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
                var gameMapStr = jsonUtils.writeMapToJson(gameMap,null);
                server.sendMessage(gameMapStr);
                server.end();
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
    void setInitUnits() {
        ArrayList<String> units = server.receiveMessage();

    }

    void OneRound() {
        var gameMapStr = jsonUtils.writeMapToJson(gameMap,null);
        server.sendMessage(gameMapStr);
        ArrayList<String> orders = server.receiveMessage();
        var orderList = new ArrayList<OrderBasic>();
        for(var str : orders) {
            orderList.addAll(jsonUtils.readJsonToOrderList(str, gameMap));
        }
        handler.execute(gameMap, orderList);
        //handler.addOne(gameMap)
    }

    public static void main(String[] args) throws IOException {
        var control = new GameServerController(6666,3);
        control.InitializeMap();

        // set init units

        while(!control.isGameDone()) {
            control.OneRound();
        }

    }


}