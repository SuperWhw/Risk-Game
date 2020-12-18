package Utilities;
import Shared.*;
import com.google.gson.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

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
    writeOrderListToJson(): ArrayList<Order> >> GameMap


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
        private PlayerJsonAdaptor() { }
        public void print() {
            System.out.println("Player\nname : " + this.name + '\n' + "territories: " + this.territories);
        }
    }

    class TerritoryJsonAdaptor {
        public String name;
        public String aliasName;
        public ArrayList<String> neighbors;

        public void print() {
            System.out.println("Territory\nname : " + this.name + '\n' + "neighbors: " + this.neighbors);
        }

        public TerritoryJsonAdaptor(Territory territory) {
            this.neighbors = new ArrayList<String>();
            this.name = territory.getName();
            this.aliasName = territory.getAliasName();
            for(Territory Territory : territory.getNeighbors()) {
                this.neighbors.add(Territory.getAliasName());
            }
        }
    }

    class GameMapJsonAdaptor {
        public ArrayList<TerritoryJsonAdaptor> territorySet;
        public ArrayList<ArrayList<String> > territoryGroup;
        public int initUnits;

        public void print() {
            System.out.println("GameMap:\nInitUnit: " + this.initUnits);
            System.out.println("territoryGroup" + this.territoryGroup);
            for(TerritoryJsonAdaptor t : this.territorySet) {
                t.print();
            }
        }

        public GameMapJsonAdaptor(GameMap gameMap, ArrayList<ArrayList<Territory>> territoryGroup) {
            this.initUnits = gameMap.getInitUnits();
            this.territorySet = new ArrayList<TerritoryJsonAdaptor>();
            this.territoryGroup = new ArrayList<ArrayList<String>>();

            for(ArrayList<Territory> Territory : territoryGroup) {
                ArrayList array = new ArrayList<String>();
                for(Territory territory : Territory) {
                    array.add(territory.getAliasName());
                }
                this.territoryGroup.add(array);
            }
            for(Territory territory : gameMap.getTerritorySet()) {
                this.territorySet.add(new TerritoryJsonAdaptor(territory));
            }
        }

        public GameMap toGameMap() {

            // create territoryList
            ArrayList<Territory> territoryList = new ArrayList<Territory>();
            for(TerritoryJsonAdaptor adaptor : this.territorySet) {
                Territory territory = new Territory(adaptor.name, adaptor.aliasName,0,null);
                territoryList.add(territory);
            }

            // create map
            GameMap gameMap = new GameMap();
            gameMap.buildMap(territoryList);

            // add neighbors
            for(TerritoryJsonAdaptor adaptor : this.territorySet) {
                Territory territory = gameMap.getTerritoryByName(adaptor.name);
                territory.setNeighbors(new HashSet<>() {{
                    for(String neighbor : adaptor.neighbors) {
                        add(gameMap.getTerritoryByName(neighbor));
                    }
                }});
            }
            return gameMap;
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

        public OrderBasic toOrderBasic(GameMap gameMap) {
            if(this.type.equals("move")) {
                return new MoveOrder(null, "move",
                        gameMap.getTerritoryByName(this.fromT),
                        gameMap.getTerritoryByName(this.toT), this.units);
            }
            else if(this.type.equals("attack")) {
                return new AttackOrder(null, "attack",
                        gameMap.getTerritoryByName(this.fromT),
                        gameMap.getTerritoryByName(this.toT), this.units);
            }
            else {
                return null;
            }
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
                OrderBasic order = adaptor.toOrderBasic(gameMap);
                if(order != null) orders.add(order);
            }
            return orders;
        }
    }

    public String writeMapToJson(GameMap gameMap, ArrayList<ArrayList<Territory>> territoryGroup) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor =  new GameMapJsonAdaptor(gameMap, territoryGroup);
        return gson.toJson(adaptor);
    }

    public GameMap readJsonToGameMap(String mapJson) {
        Gson gson = new Gson();
        GameMapJsonAdaptor adaptor = gson.fromJson(mapJson, GameMapJsonAdaptor.class);
        return adaptor.toGameMap();
    }

    public String writeOrderListToJson(ArrayList<OrderBasic> orders) {
        Gson gson = new Gson();
        OrderBasicListJsonAdapter adaptor = new OrderBasicListJsonAdapter(orders);
        return gson.toJson(adaptor);
    }

    public ArrayList<OrderBasic> readJsonToOrderList(String ordersJson) {
        Gson gson = new Gson();
        OrderBasicListJsonAdapter adaptor = gson.fromJson(ordersJson, OrderBasicListJsonAdapter.class);
        return adaptor.toOrderList();
    }

    public static void main(String[] argv) {
        GameJsonUtils utils = new GameJsonUtils();
        FileIOBasics fileIO = new FileIOBasics();
        String mapStr = fileIO.readJsonFile("map.json");
        GameMap gameMap = utils.readJsonToGameMap(mapStr);
    }
}