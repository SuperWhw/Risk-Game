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
        client = new BasicTCPClient(port, ipv4, hostname);
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
        gameMap = jsonUtils.readJsonToGameMap(MapStr, null);
        viewer.printMap(gameMap, own, "simple");

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
            t.setUnits(Integer.parseInt(strArray[index]));
        }

        // send player to server
        client.sendMessage(own.getName());
    }

    void OneRound() {
        String MapStr = client.receiveMessage();
        gameMap = jsonUtils.readJsonToGameMap(MapStr, null); // TODO: updates?

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
                orderList.add(order); // where?
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format!");
            } catch (Exception e) {
                System.out.println();
            }
            System.out.println();
        }
        String orderStr = jsonUtils.writeOrderListToJson(orderList);
        client.sendMessage(orderStr);
    }

    boolean isGameDone(){
        if(own.getTerritories().size() == gameMap.getTerritoryMap().size()) {
            System.out.println("Congras, you win!");
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