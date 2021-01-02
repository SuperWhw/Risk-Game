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
            server.CreateSockets();
        }
        catch (IOException e) {
            printer.printWithColor("TCP Server build failure, please check your network",
                    ColorPrint.Color.RED);
        }
    }

    public boolean isGameDone(){
        if(server.getRunningNum() <= 1) {
            return true;
        }
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
        server.sendMessage("Hello this is your host, please input your name:");

        // create n players
        var players = server.receiveMessage();
        System.out.println("Message received " + players);

        // read and load map.json
        String fileName = this.getClass().getClassLoader().getResource("map.json").getPath();
        var MapStr = fileIO.readJsonFile(fileName);
        gameMap = jsonUtils.readJsonToGameMap(MapStr, players);

    }
    void setInitUnits() {
        var gameMapStr = jsonUtils.writeMapToJson(gameMap, null);
        server.sendMessage(gameMapStr);
        ArrayList<String> playerWithUnits = server.receiveMessage();
        System.out.println("units received " + playerWithUnits);
        jsonUtils.readUnits(playerWithUnits, gameMap);
        for(var player : gameMap.getPlayerMap().values()) {
            boolean isNull = true;
            for(var territory : player.getTerritories()) {
                isNull &= (territory.getUnits() == 0);
            }
            if(isNull) {
                for(var territory : player.getTerritories()) {
                    territory.setUnits(gameMap.getInitUnits()/3);
                }
            }
        }
    }
    void sendMap() {
        if(server.getRunningNum() <= 1) {
            return;
        }
        var gameMapStr = jsonUtils.writeMapToJson(gameMap,null);
        server.sendMessage(gameMapStr);
    }
    void OneRound() {
        for(var player : gameMap.getPlayerMap().values()) {
            viewer.printMap(gameMap, player, "order");
            break;
        }

        sendMap();

        ArrayList<String> orders = server.receiveMessage();
        // print order
        System.out.println("orderStr is: ");
        for(var order: orders) {
            System.out.println(order);
        }
        var orderList = new ArrayList<OrderBasic>();
        for(var str : orders) {
            var orderBasic = jsonUtils.readJsonToOrderList(str, gameMap);
            if(orderBasic != null)
                orderList.addAll(orderBasic);
        }

        // print order
        System.out.println("order is: ");
        for(var order: orderList) {
            System.out.printf("%s %d units from %s to %s\n",order.getOrderType(),order.getUnits(),order.getFromT().getName(),order.getToT().getName());
        }
        System.out.println();

        handler.execute(orderList);
        handler.addOne(gameMap);
    }

    public void end() {
        server.end();
    }

    public static void main(String[] args) throws IOException {
        var control = new GameServerController(6666,3);
        control.InitializeMap();
        control.setInitUnits();

        while(!control.isGameDone()) {
            control.OneRound();
        }
        control.sendMap();
        control.end();
    }
}