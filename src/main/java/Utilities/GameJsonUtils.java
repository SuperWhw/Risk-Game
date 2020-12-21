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

/*
    Here is the class than transform between Player/Territory/OrderBasic and JsonStr
    com.google.gson is used as public json library
    In order to better interact with json, 3 intermediate/adaptor inner classes are created to load JsonStr
    they are:

    class PlayerJsonAdaptor

    class TerritoryJsonAdaptor

    class GameMapJsonAdaptor

    Three important interfaces are given:

    readMapToJson(): GameMap >>> JsonStr
    writeJsonToGameMap(): JsonStr >>> GameMap
    writeOrderListToJson(): ArrayList<Order> >> JsonStr
    readJsonToOrderList(): JsonStr >> ArrayList<Order>

    -----------------------
    init_writeMapToJson()
    ----------------------- only used for initialization

    by 2020.12.21 Shanghai
 */
public class GameJsonUtils {

    class PlayerJsonAdaptor {
        public String name;
        public ArrayList<String> territories;
        public PlayerJsonAdaptor(Player player) {
            this.name = player.getName();
            for(Territory territory : player.getTerritories()) {
                this.territories.add(territory.getAliasName());
            }
        }
        public Player toPlayer(GameMap gameMap) {
            var player = new Player(this.name);
            for(var territory : this.territories) {
                var Territory = gameMap.getTerritoryByName(territory);
                if(Territory != null)
                    player.addTerritory(Territory);
            }
            return player;
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
            System.out.println("Territory\nname : " + this.name + '\n' + "neighbors: " + this.neighbors);
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

            // no need to initialize player in the beginning, map.jason doesn't contain that
            if(withPlayerInfo && !gameMap.getPlayerList().isEmpty()) {
                this.players = new ArrayList<String>();
                for (var player : gameMap.getPlayerList().values()) {
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
            for(Territory territory : gameMap.getTerritorySet()) {
                this.territorySet.add(new TerritoryJsonAdaptor(territory, withPlayerInfo));
            }
        }

        // build a new Game Map: used for GameMap initialization.
        public GameMap toGameMap(ArrayList<String> playerList) {

            // create players
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
            gameMap.buildMap(territoryList);
            gameMap.setPlayers(players);

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
            }

            // bound players with territories
            for(int i = 0; i < playerList.size(); ++i) {
                for(int j = 0; j < this.territoryGroup.size(); ++j) {
                    gameMap.getTerritoryByName(this.territoryGroup.get(i).get(j)).
                            setOwner(gameMap.getPlayerByName(playerList.get(i)));
                }
            }

            return gameMap;
        }

        public void updateMap(GameMap gameMap) {

            //update territory and its owner
            for(var adaptor : territorySet) {
                var territory = gameMap.getTerritoryByName(adaptor.aliasName);
                territory.setUnits(adaptor.units);
                Player player = gameMap.getPlayerByName(adaptor.owner);

                if(!player.hasTerritory(territory)) {
                    territory.setOwner(player);
                }
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

    public String init_writeMapToJson(GameMap gameMap, ArrayList<ArrayList<Territory>> territoryGroup) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor =  new GameMapJsonAdaptor(gameMap, territoryGroup, false);
        return gson.toJson(adaptor);
    }

    public GameMap readJsonToGameMap(String mapJson, ArrayList<String> players) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor = gson.fromJson(mapJson, GameMapJsonAdaptor.class);
        return adaptor.toGameMap(players);
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
        for(var player : gameMap.getPlayerList().values()) {
            viewer.printMap(gameMap, player, "order");
        }

        var MapStrAfterOrders = jsonUtil.writeMapToJson(gameMap,null);
        System.out.println(MapStrAfterOrders);

    }
}