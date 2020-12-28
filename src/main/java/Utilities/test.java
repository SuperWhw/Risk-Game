package Utilities;

public class test {
    public static void main(String[] args) {
        for(int i = 30; i <= 40; ++i) {
            System.out.println("\033[" + i + ";1m" + "hello world" + "\033[0m");
        }
        for(int i = 90; i <= 98; ++i) {
            System.out.println("\033[" + i + ";1m" + "hello world" + "\033[0m");
        }
        for(int i = 30; i <= 40; ++i) {
            System.out.println("\033[" + i + ";4m" + "hello world" + "\033[0m");
        }
        for(int i = 90; i <= 98; ++i) {
            System.out.println("\033[" + i + ";4m" + "hello world" + "\033[0m");
        }
        /* test GameJsonUtils
        ArrayList<String> players = new ArrayList<>();
        players.add("pabc");
        players.add("pls");
        players.add("ple");

        var fileIO = new FileIOBasics();
        var MapStr = fileIO.readJsonFile("map.json");

        var jsonUtil = new GameJsonUtils();
        var gameMap = jsonUtil.readJsonToGameMap(MapStr, players);

        var viewer = new GameClientViewer();

        var MapStrAfterOrders = jsonUtil.writeMapToJson(gameMap,null);

        System.out.println(MapStrAfterOrders);

        GameMap gameMap1 = jsonUtil.readJsonToGameMap(MapStrAfterOrders, null);

        var p1 = gameMap1.getPlayerByName("pabc");

        var p2 = gameMap1.getPlayerByName("pls");

        var p3 = gameMap1.getPlayerByName("ple");

        for(Territory territory : p1.getTerritories()) {
            territory.setUnits(13);
        }
        for(Territory territory : p2.getTerritories()) {
            territory.setUnits(14);
        }
        for(Territory territory : p3.getTerritories()) {
            territory.setUnits(15);
        }

        var Str1 = jsonUtil.writeUnits(p1);
        var Str2 = jsonUtil.writeUnits(p2);
        var Str3 = jsonUtil.writeUnits(p3);
        ArrayList<String> strs = new ArrayList<>();
        strs.add(Str1);
        strs.add(Str2);
        strs.add(Str3);
        jsonUtil.readUnits(strs, gameMap);

        // here is the order test
        MoveOrder m = new MoveOrder(p1, "move", gameMap.getTerritoryByName("N"),
                gameMap.getTerritoryByName("O"), 6);
        AttackOrder a = new AttackOrder(p1, "attack", gameMap.getTerritoryByName("O"),
                gameMap.getTerritoryByName("S"), 16);
        var orders = new ArrayList<OrderBasic>();
        orders.add(m);
        orders.add(a);
        String orderStr = jsonUtil.writeOrderListToJson(orders);
        System.out.println("orderStr: " + orderStr);
        var handler = new Server.OrderHandler();

        viewer.printMap(gameMap, gameMap.getPlayerByName("pabc"), "order");

        handler.execute(orders);

        viewer.printMap(gameMap, gameMap.getPlayerByName("pabc"), "simple");

        System.out.println(jsonUtil.writeMapToJson(gameMap,null));
        */
    }

}

