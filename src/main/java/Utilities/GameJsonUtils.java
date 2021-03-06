package Utilities;
import Shared.*;
import com.google.gson.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.*;


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
            territories = new ArrayList<>();
            units = new ArrayList<>();
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
            this.neighbors = new ArrayList<>();
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
            this.territorySet = new ArrayList<>();
            this.territoryGroup = new ArrayList<>();

            // no need to initialize player in the beginning, map.json doesn't contain that
            if(withPlayerInfo && !gameMap.getPlayerMap().isEmpty()) {
                this.players = new ArrayList<>();
                for (var player : gameMap.getPlayerMap().values()) {
                    this.players.add(player.getName());
                }
            }

            // no need to load territoryGroup after initialization
            if(territoryGroup != null) {
                this.territoryGroup = new ArrayList<>();
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
            if(gameMap == null) return;
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
            String name = this.type.substring(0, 1).toUpperCase() + this.type.substring(1);
            if (this.type.equals("move") || this.type.equals("attack")) {
                try {
                    Class a = Class.forName("Shared." + name + "Order");
                    Class[] classes = new Class[] {Player.class, String.class, Territory.class, Territory.class, int.class};
                    return a.getDeclaredConstructor(classes).newInstance(
                            gameMap.getPlayerByName(this.player), this.type,
                            gameMap.getTerritoryByName(this.fromT),
                            gameMap.getTerritoryByName(this.toT), this.units);
                }
                catch (ClassNotFoundException e) {
                    System.out.println("Class: " + this.type + " not found");
                }
                catch (NoSuchMethodException e) {
                    System.out.println("Class: " + this.type + " no such method");
                }
                catch (InstantiationException e) {
                    System.out.println("Class: " + this.type + " instantiation exception");
                }
                catch (IllegalAccessException e) {
                    System.out.println("Class: " + this.type + " illegal-access exception");
                }
                catch (IllegalArgumentException | InvocationTargetException e) {
                    System.out.println("Class: " + this.type + " illegal-argument exception");
                }
            }
            return null;
        }
        public void print() {
            System.out.println("player: " + player + " from " + fromT + " to " +  toT + " units " + units + " type " +  type);
        }
    }

    class OrderBasicListJsonAdapter {
        public ArrayList<OrderBasicJsonAdapter> orderBasics;

        public OrderBasicListJsonAdapter(ArrayList<OrderBasic> orders) {
            this.orderBasics = new ArrayList<>();
            for(OrderBasic order : orders) {
                orderBasics.add(new OrderBasicJsonAdapter(order));
            }
        }

        public ArrayList<OrderBasic> toOrderList(GameMap gameMap) {
            ArrayList<OrderBasic> orders = new ArrayList<>();
            for(OrderBasicJsonAdapter adaptor : this.orderBasics) {
                Object order = adaptor.toOrderBasic(gameMap);
                if((order instanceof OrderBasic)) orders.add((OrderBasic)order);
            }
            return orders;
        }
        public void print() {
            for(var adaptor : orderBasics) {
                adaptor.print();
            }
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
        if(mapJson == null) return null;
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor = gson.fromJson(mapJson, GameMapJsonAdaptor.class);
        return adaptor.toGameMap(players);
    }

    public void updateMap(String mapJson, GameMap gameMap) {
        if(mapJson == null) return;
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor = gson.fromJson(mapJson, GameMapJsonAdaptor.class);
        if(adaptor == null) return;
        adaptor.updateMap(gameMap);
    }

    public String writeOrderListToJson(ArrayList<OrderBasic> orders) {
        Gson gson = new Gson();
        OrderBasicListJsonAdapter adaptor = new OrderBasicListJsonAdapter(orders);
        return gson.toJson(adaptor);
    }

    public ArrayList<OrderBasic> readJsonToOrderList(String ordersJson, GameMap gameMap) {
        if(ordersJson == null) return null;
        Gson gson = new Gson();
        OrderBasicListJsonAdapter adaptor = gson.fromJson(ordersJson, OrderBasicListJsonAdapter.class);
        return adaptor.toOrderList(gameMap);
    }

    public void readUnits(ArrayList<String> TerritoryWithUnitsStr, GameMap gameMap) {
        Gson gson = new Gson();
        for(String jsonStr : TerritoryWithUnitsStr) {
            if(jsonStr == null) return;
            var adaptor = gson.fromJson(jsonStr, PlayerJsonAdaptor.class);
            adaptor.updateMap(gameMap);
        }
    }

    public String writeUnits(Player player) {
        Gson gson = new Gson();
        var adaptor = new PlayerJsonAdaptor(player);
        return gson.toJson(adaptor);
    }

}