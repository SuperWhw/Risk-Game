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

    GameClientController (int port, String hostname) {
        printer = new ColorPrint();
        fileIO = new FileIOBasics();
        jsonUtils = new GameJsonUtils();
        viewer = new GameClientViewer();
        scanner = new Scanner(System.in);
        checker = new CheckHelper();
        handler = new OrderHandler();
        gsu = new GameStringUtils();
        client = new BasicTCPClient(port, hostname);
        client.buildConnection();
    }

    void setName() {
        System.out.println(client.receiveMessage());
        String name;
        do {
            name = scanner.nextLine();
        } while(!checker.checkName(name));
        client.sendMessage(name);
        System.out.println("Waiting for other players...");

        //build player
        own = new Player(name);
    }

    void InitializeMap() {
        // receive map
        String MapStr = client.receiveMessage();
        // System.out.println(MapStr);
        gameMap = jsonUtils.readJsonToGameMap(MapStr, null);
        System.out.println("\nThis is the original map: \n");
        viewer.printMap(gameMap, own, "simple");
        System.out.println("Please input initial units for your each territory: ");

        int initUnits = gameMap.getInitUnits();
        var territoryList = gameMap.getPlayerByName(own.getName()).getTerritories();
        String initUnitsList;
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

        /* For test
        System.out.println("Now you have: ");
        for(var t: own.getTerritories()) {
            System.out.print(t.getName() + ' ');
            System.out.println(t.getUnits());
        }
        */

        // send player to server
        String ownStr = jsonUtils.writeUnits(own);
        client.sendMessage(ownStr);
        System.out.println("Waiting for other players...");
    }

    int OneRound(int round) {
        String MapStr = client.receiveMessage();
        if(MapStr == null) {
            //System.out.println("Server disconnected, game end");
            return 1;
        }
        jsonUtils.updateMap(MapStr, gameMap);
        own = gameMap.getPlayerByName(own.getName());

        System.out.printf("\n>>>>>>>>>>>>>>>>>>>>> Round %d <<<<<<<<<<<<<<<<<<<<<\n\n",round);
        int status = getStatus();
        ArrayList<OrderBasic> orderList = new ArrayList<>();
        if(status == 0) {
            String in;
            while (true) {
                try {
                    viewer.printMap(gameMap, own, "order");
                    System.out.println("Please input order: ");
                    in = scanner.nextLine();
                    if (in.equals("commit")) break;
                    OrderBasic order = gsu.strToOrder(gameMap, in, own);
                    handler.execute(order);
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
            for (var order : orderList) {
                System.out.printf("%s %d units from %s to %s\n", order.getOrderType(), order.getUnits(), order.getFromT().getName(), order.getToT().getName());
            }
            System.out.println();
        }
        else if(status == -1){
            System.out.println("You are watching the game: ");
            viewer.printMap(gameMap, own, "order");
        }
        else {
            return status;
        }

        // send order list
        String orderStr = jsonUtils.writeOrderListToJson(orderList);
        client.sendMessage(orderStr);

        System.out.println("Waiting for other players...");
        return status;
    }

    // get status:
    // status = 1: Has winner(you or other player), game finish
    // status = -1: You lose
    // status = 0: Game continue
    int getStatus(){
        if(own.getTerritories().size() == gameMap.getTerritoryMap().size()) {
            System.out.println("Congrats, you win!");
            return 1;
        }
        else if (own.getTerritories().size() == 0) {
            System.out.print("Sorry, you lose! ");
            Player winner = hasWinner();
            if(winner != null) {
                System.out.printf("The winner is %s\n",winner.getName());
                return 1;
            }
            return -1;
        }
        return 0;
    }

    Player hasWinner() {
        for(var p: gameMap.getPlayerMap().values()) {
            if(p.getTerritories().size() == gameMap.getTerritoryMap().size()) return p;
        }
        return null;
    }

    public static void main(String[] args) {

        var control = new GameClientController(6666, "riskgame.top");

        control.setName();
        control.InitializeMap();

        int round = 1, status = 0;
        while(control.client.isRunning() && status != 1) {
            //System.out.println("isRunning " + control.client.isRunning() + " status " + status);
            status = control.OneRound(round++);
        }
        control.client.end();
    }
}