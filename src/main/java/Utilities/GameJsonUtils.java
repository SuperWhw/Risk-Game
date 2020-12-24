package Utilities;
import Client.GameClientViewer;
import Shared.*;
import com.google.gson.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.*;
import java.util.Map;

/*
    Here is the class than transform between Player/Territory/OrderBasic and JsonStr
    com.google.gson is used as public json library
    In order to better interact with json,
    3 intermediate/adaptor inner classes are created to load JsonStr:

    class PlayerJsonAdaptor

    class TerritoryJsonAdaptor

    class GameMapJsonAdaptor

    6 important interfaces are given:

    readMapToJson(): GameMap >>> JsonStr

    writeJsonToGameMap(): JsonStr >>> GameMap

    writeOrderListToJson(ArrayList): ArrayList<Order> >> JsonStr

    readJsonToOrderList(): JsonStr >> ArrayList<Order>

    writeUnits(Player): Player >> JsonStr

    readUnits(ArrayList, GameMap): JsonStr >> update GameMapUnits

    -----------------------
    init_readMapToJson(GameMap): GameMap >> JsonStr
    ----------------------- only used for initialization without players info

    2020.12.21-2020.12.23 IN Shanghai
 */
public class GameJsonUtils {

    class PlayerJsonAdaptor {
        public String name;
        public ArrayList<String> territories;
        public ArrayList<Integer> units;

        public PlayerJsonAdaptor(Player player) {
            this.name = player.getName();
            territories = new ArrayList<String>();
            units = new ArrayList<Integer>();
            for(Territory territory : player.getTerritories()) {
                this.territories.add(territory.getAliasName());
                this.units.add(territory.getUnits());
            }
        }

        public void updateMap(GameMap gameMap) {
            for(int i = 0; i < territories.size(); ++i) {
                gameMap.getTerritoryByName(territories.get(i)).setUnits(units.get(i));
            }
        }

        public void print() {
            System.out.println("Player\nname : " + this.name + '\n' + "territories: " + this.territories);
        }
    }

    class TerritoryJsonAdaptor {
        public String name;
        public String aliasName;
        public ArrayList<String> neighbors;
        public String owner;
        public int units;

        public void print() {
            System.out.println("Territory\nname : " + this.name + "\nneighbors: " + this.neighbors +
                    "\nowners: " + this.owner);
        }

        public TerritoryJsonAdaptor(Territory territory, boolean withPlayerInfo) {
            this.neighbors = new ArrayList<String>();
            this.name = territory.getName();
            this.aliasName = territory.getAliasName();
            if(withPlayerInfo) this.owner = territory.getOwner().getName();
            this.units = territory.getUnits();
            for(Territory Territory : territory.getNeighbors()) {
                this.neighbors.add(Territory.getAliasName());
            }
        }
    }

    class GameMapJsonAdaptor {
        public ArrayList<TerritoryJsonAdaptor> territorySet;
        public ArrayList<ArrayList<String> > territoryGroup;
        public ArrayList<String> players;
        public int initUnits;

        public void print() {
            System.out.println("GameMap:\nInitUnit: " + this.initUnits);
            System.out.println("territoryGroup" + this.territoryGroup);
            System.out.println("players:" + this.players);
            for(TerritoryJsonAdaptor t : this.territorySet) {
                t.print();
            }
        }

        public GameMapJsonAdaptor(GameMap gameMap, ArrayList<ArrayList<Territory>> territoryGroup,
                                  boolean withPlayerInfo) {
            this.initUnits = gameMap.getInitUnits();
            this.territorySet = new ArrayList<TerritoryJsonAdaptor>();
            this.territoryGroup = new ArrayList<ArrayList<String>>();

            // no need to initialize player in the beginning, map.json doesn't contain that
            if(withPlayerInfo && !gameMap.getPlayerMap().isEmpty()) {
                this.players = new ArrayList<String>();
                for (var player : gameMap.getPlayerMap().values()) {
                    this.players.add(player.getName());
                }
            }

            // no need to load territoryGroup after initialization
            if(territoryGroup != null) {
                this.territoryGroup = new ArrayList<ArrayList<String>>();
                for (ArrayList<Territory> Territory : territoryGroup) {
                    var array = new ArrayList<String>();
                    for (Territory territory : Territory) {
                        array.add(territory.getAliasName());

                    }
                    this.territoryGroup.add(array);
                }
            }

            // always need to load territories info
            for(Territory territory : gameMap.getTerritoryMap().values()) {
                this.territorySet.add(new TerritoryJsonAdaptor(territory, withPlayerInfo));
            }
        }

        // build a new Game Map: used for GameMap initialization.
        public GameMap toGameMap(ArrayList<String> playerList) {
            // create players
            if(playerList == null) playerList = this.players;
            var players = new ArrayList<Player>();
            for(String playerName : playerList) {
                players.add(new Player(playerName));
            }

            // create territoryList
            var territoryList = new ArrayList<Territory>();
            for(TerritoryJsonAdaptor adaptor : this.territorySet) {
                var territory = new Territory(adaptor.name, adaptor.aliasName,0);
                territoryList.add(territory);
            }

            // create map
            GameMap gameMap = new GameMap();
            gameMap.setTerritoryMap(territoryList);
            gameMap.setPlayerMap(players);

            // for each territory, add neighbors
            for(var adaptor : this.territorySet) {
                Territory territory = gameMap.getTerritoryByName(adaptor.aliasName);
                if(territory == null) {
                    System.out.println("getTerritory failure");
                    continue;
                }
                territory.setNeighbors(new HashSet<>() {{
                    for(String neighbor : adaptor.neighbors) {
                        add(gameMap.getTerritoryByName(neighbor));
                    }
                }});

                // this is used without territoryGroup
                if(this.territoryGroup.size() == 0) {
                    gameMap.getTerritoryByName(adaptor.aliasName).
                            setOwner(gameMap.getPlayerByName(adaptor.owner));
                }
            }

            // bound players with territories while initialization
            for(int i = 0; i < playerList.size(); ++i) {
                for(int j = 0; j < this.territoryGroup.size(); ++j) {
                    gameMap.getTerritoryByName(this.territoryGroup.get(i).get(j)).
                            setOwner(gameMap.getPlayerByName(playerList.get(i)));
                }
            }

            gameMap.setInitUnits(initUnits);

            return gameMap;
        }

        public void updateMap(GameMap gameMap) {

            //update territory and its owner
            for(var adaptor : territorySet) {
                var t = gameMap.getTerritoryByName(adaptor.aliasName);
                t.setUnits(adaptor.units);
                t.setOwner(gameMap.getPlayerByName(adaptor.owner));
                t.setMoveLock(false);
                t.setAttackLock(false);
            }
        }

    }

    class OrderBasicJsonAdapter {
        public String player;
        public String fromT;
        public String toT;
        public int units;
        public String type;

        public OrderBasicJsonAdapter(OrderBasic order) {
            this.player = order.getPlayer().getName();
            this.fromT = order.getFromT().getAliasName();
            this.toT = order.getToT().getAliasName();
            this.units = order.getUnits();
            this.type = order.getOrderType();
        }

        public Object toOrderBasic(GameMap gameMap) {
            if (this.type.equals("MoveOrder") || this.type.equals("AttackOrder")) {
                try {
                    return Class.forName(this.type).getDeclaredConstructor().newInstance(
                            null, this.type,
                            gameMap.getTerritoryByName(this.fromT),
                            gameMap.getTerritoryByName(this.toT), this.units);
                } catch (Exception e) {
                    System.out.println("Class: " + this.type + " create failure");
                }
            }
            return null;
        }
    }

    class OrderBasicListJsonAdapter {
        public ArrayList<OrderBasicJsonAdapter> orderBasics;

        public OrderBasicListJsonAdapter(ArrayList<OrderBasic> orders) {
            this.orderBasics = new ArrayList<OrderBasicJsonAdapter>();
            for(OrderBasic order : orders) {
                orderBasics.add(new OrderBasicJsonAdapter(order));
            }
        }

        public ArrayList<OrderBasic> toOrderList(GameMap gameMap) {
            ArrayList<OrderBasic> orders = new ArrayList<OrderBasic>();
            for(OrderBasicJsonAdapter adaptor : this.orderBasics) {
                Object order = adaptor.toOrderBasic(gameMap);
                if(order != null && (order instanceof OrderBasic)) orders.add((OrderBasic)order);
            }
            return orders;
        }
    }

    public String writeMapToJson(GameMap gameMap, ArrayList<ArrayList<Territory>> territoryGroup) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor =  new GameMapJsonAdaptor(gameMap, territoryGroup, true);
        return gson.toJson(adaptor);
    }

    public String init_readMapToJson(GameMap gameMap, ArrayList<ArrayList<Territory>> territoryGroup) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor =  new GameMapJsonAdaptor(gameMap, territoryGroup, false);
        return gson.toJson(adaptor);
    }

    public GameMap readJsonToGameMap(String mapJson, ArrayList<String> players) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor = gson.fromJson(mapJson, GameMapJsonAdaptor.class);
        return adaptor.toGameMap(players);
    }

    public void updateMap(String mapJson, GameMap gameMap) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor = gson.fromJson(mapJson, GameMapJsonAdaptor.class);
        adaptor.updateMap(gameMap);
    }

    public String writeOrderListToJson(ArrayList<OrderBasic> orders) {
        Gson gson = new Gson();
        OrderBasicListJsonAdapter adaptor = new OrderBasicListJsonAdapter(orders);
        return gson.toJson(adaptor);
    }

    public ArrayList<OrderBasic> readJsonToOrderList(String ordersJson, GameMap gameMap) {
        Gson gson = new Gson();
        OrderBasicListJsonAdapter adaptor = gson.fromJson(ordersJson, OrderBasicListJsonAdapter.class);
        return adaptor.toOrderList(gameMap);
    }

    public void readUnits(ArrayList<String> TerritoryWithUnitsStr, GameMap gameMap) {
        Gson gson = new Gson();
        for(String jsonStr : TerritoryWithUnitsStr) {
            System.out.println(jsonStr);
            var adaptor = gson.fromJson(jsonStr, PlayerJsonAdaptor.class);
            adaptor.updateMap(gameMap);
        }
    }

    public String writeUnits(Player player) {
        Gson gson = new Gson();
        var adaptor = new PlayerJsonAdaptor(player);
        return gson.toJson(adaptor);
    }

    public static void main(String[] argv) {
        ArrayList<String> players = new ArrayList<String>();
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
        ArrayList<String> strs = new ArrayList<String>();
        strs.add(Str1);
        strs.add(Str2);
        strs.add(Str3);
        jsonUtil.readUnits(strs, gameMap);
        viewer.printMap(gameMap, gameMap.getPlayerByName("pabc"), "order");

    }
}