package Client;

import Shared.*;
import Utilities.ColorPrint;
import Utilities.FileIOBasics;
import Utilities.GameJsonUtils;
import Utilities.GameStringUtils;

import java.util.ArrayList;
import java.util.Scanner;

public class GameClientController {
    Player own;
    BasicTCPClient client;
    ColorPrint printer;
    GameMap gameMap;
    FileIOBasics fileIO;
    GameJsonUtils jsonUtils;
    GameClientViewer viewer;
    Scanner scanner;
    GameStringUtils gsu;
    OrderHandler handler;
    CheckHelper checker;

    GameClientController (int port, String ipv4, String hostname) {
        printer = new ColorPrint();
        fileIO = new FileIOBasics();
        jsonUtils = new GameJsonUtils();
        viewer = new GameClientViewer();
        scanner = new Scanner(System.in);
        checker = new CheckHelper();
        handler = new OrderHandler();
        gsu = new GameStringUtils();
        client = new BasicTCPClient(port, ipv4, hostname);
        client.buildConnection();
    }

    void setName() {
        System.out.println(client.receiveMessage());
        String name = "";
        do {
            name = scanner.nextLine();
        } while(!checker.checkName(name));
        client.sendMessage(name);

        //build player
        own = new Player(name);
    }

    void InitializeMap() {
        // receive map
        String MapStr = client.receiveMessage();
        // System.out.println(MapStr);
        gameMap = jsonUtils.readJsonToGameMap(MapStr, null);
        System.out.println("This is the original map: \n");
        viewer.printMap(gameMap, own, "simple");
        System.out.println("Please input initial units for your each territory: ");

        int initUnits = gameMap.getInitUnits();
        var territoryList = gameMap.getPlayerByName(own.getName()).getTerritories();
        String initUnitsList = "";
        do {
            initUnitsList = scanner.nextLine();
        } while(!checker.checkInitUnitsList(initUnitsList,initUnits,territoryList.size()));
        String[] strArray = initUnitsList.split(" ");

        // update territory
        int index = 0;
        for(var t: territoryList) {
            int unit = Integer.parseInt(strArray[index++]);
            Territory newt = new Territory(t.getName(),t.getAliasName(),unit,own);
            own.addTerritory(newt);
        }

        System.out.println("Now you have: ");
        for(var t: own.getTerritories()) {
            System.out.print(t.getName() + ' ');
            System.out.println(t.getUnits());
        }


        // send player to server
        String ownStr = jsonUtils.writeUnits(own);
        System.out.println(ownStr);
        client.sendMessage(ownStr);
    }

    void OneRound() {
        String MapStr = client.receiveMessage();
        System.out.println("received map is " + MapStr);
        jsonUtils.updateMap(MapStr, gameMap);

        ArrayList<OrderBasic> orderList = new ArrayList<>();
        String in;
        while(true) {
            try {
                viewer.printMap(gameMap, own, "order");
                System.out.println("Please input order: ");
                in = scanner.nextLine();
                if(in.equals("commit")) break;
                OrderBasic order = gsu.strToOrder(gameMap, in, own);
                handler.execute(gameMap, order);
                orderList.add(order);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format!");
            } catch (Exception e) {
                System.out.println();
            }
            System.out.println();
        }

        // print order
        System.out.println("You order is: ");
        for(var order: orderList) {
            System.out.printf("%s %d units from %s to %s\n",order.getOrderType(),order.getUnits(),order.getFromT().getName(),order.getToT().getName());
        }
        System.out.println();

        // send order list
        String orderStr = jsonUtils.writeOrderListToJson(orderList);
        client.sendMessage(orderStr);
    }

    boolean isGameDone(){
        if(own.getTerritories().size() == gameMap.getTerritoryMap().size()) {
            System.out.println("Congrats, you win!");
            return true;
        }
        else if (own.getTerritories().size() == 0) {
            System.out.println("Sorry, you lose!");
        }
        return false;
    }

    public static void main(String[] args) {
        var control = new GameClientController(6666, "127.0.0.1", "localhost");
        control.setName();
        control.InitializeMap();
        while(!control.isGameDone()) {
            control.OneRound();
        }

    }
}