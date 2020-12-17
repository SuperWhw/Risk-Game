package Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import Client.*;

import java.util.ArrayList;
import java.util.HashSet;

public class GameJsonUtils {
    private String str;
    public void print() {
        System.out.println(str);
    }
    public GameJsonUtils() {
        this.str = "";
    }
    public GameJsonUtils(String str) {
        this.str = str;
    }
    private JsonObject toObject(OrderBasic order) {
        JsonObject object = new JsonObject();
        object.addProperty("name", order.getPlayer().getName());
        object.addProperty("orderType", order.getOrderType());
        object.addProperty("fromT", order.getFromT().getName());
        object.addProperty("toT", order.getToT().getName());
        object.addProperty("units",order.getUnits());
        return object;
    }
    private JsonObject toObject(Territory territory, boolean full) {
        JsonObject object = new JsonObject();
        object.addProperty("name", territory.getName());
        object.addProperty("units",territory.getUnits());
        object.addProperty("owner",territory.getOwner().getName());
        if(full == true) {
            object.add("neighbors", toNeighborsObject(territory.getNeighbors()));
        }
        return object;
    }
    private JsonArray toNeighborsObject(HashSet<Territory> territorySet) {
        JsonArray jsonArray = new JsonArray();
        for(Territory territory : territorySet) {
            JsonObject object = new JsonObject();
            object.addProperty("name",territory.getName());
            jsonArray.add(object);
        }
        return jsonArray;
    }

    private JsonArray toArray(HashSet<Territory> territorySet, boolean full) {
        JsonArray jsonArray = new JsonArray();
        for(Territory territory: territorySet) {
            jsonArray.add(this.toObject(territory, full));
        }
        return jsonArray;
    }

    private JsonObject toObject(HashSet<Territory> territorySet, boolean full) {
        JsonObject object = new JsonObject();
        object.add("territorySet", this.toArray(territorySet,full));
        return object;
    }

    private JsonObject toObject(GameMap gameMap) {
        JsonObject object = new JsonObject();
        object.add("territorySet",this.toObject(gameMap.getTerritorySet(),true));
        object.addProperty("Map",gameMap.getInitUnits());
        return object;
    }

    private JsonArray toArray(OrderBasic[] orderList) {
        JsonArray array = new JsonArray();
        for(OrderBasic order : orderList) {
            array.add(this.toObject(order));
        }
        return array;
    }
    private JsonObject toObject(OrderBasic[] orderList) {
        JsonObject object=  new JsonObject();
        object.add("orderList", this.toArray(orderList));
        return object;
    }

    private JsonObject toObject(Player player) {
        JsonObject object = new JsonObject();
        object.addProperty("name",player.getName());
        return object;
    }

    public void toString(OrderBasic[] orderList) {
        Gson gson = new Gson();
        this.str = gson.toJson(this.toObject(orderList));
    }

    public void toString(GameMap gameMap) {
        Gson gson = new Gson();
        this.str = gson.toJson(this.toObject(gameMap));
    }

    public void toString(HashSet<Territory> territories) {
        Gson gson = new Gson();
        this.str = gson.toJson(this.toObject(territories,false));
    }

    public static void main(String[] args) {
        GameJsonUtils gju = new GameJsonUtils();

        // create players
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");

        // create territories
        Territory Narnia = new Territory("Narnia", 10, p1);
        Territory Midkemia = new Territory("Midkemia", 12, p1);
        Territory Oz = new Territory("Oz", 8, p1);
        Territory Elantris = new Territory("Elantris", 6, p2);
        Territory Scadrial = new Territory("Scadrial", 5, p2);
        Territory Roshar = new Territory("Roshar", 3, p2);
        Territory Gondor = new Territory("Gondor", 13, p3);
        Territory Mordor = new Territory("Mordor", 14, p3);
        Territory Hogwarts = new Territory("Hogwarts", 3, p3);

        Narnia.setNeighbors(new HashSet<>() {{
            add(Midkemia);
            add(Elantris);
        }});
        Midkemia.setNeighbors(new HashSet<>() {{
            add(Narnia);
            add(Elantris);
            add(Scadrial);
            add(Oz);
        }});
        Oz.setNeighbors(new HashSet<>() {{
            add(Midkemia);
            add(Scadrial);
            add(Mordor);
            add(Gondor);
        }});
        Elantris.setNeighbors(new HashSet<>() {{
            add(Narnia);
            add(Midkemia);
            add(Scadrial);
            add(Roshar);
        }});
        Scadrial.setNeighbors(new HashSet<>() {{
            add(Elantris);
            add(Midkemia);
            add(Oz);
            add(Mordor);
            add(Hogwarts);
            add(Roshar);
        }});
        Roshar.setNeighbors(new HashSet<>() {{
            add(Elantris);
            add(Scadrial);
            add(Hogwarts);
        }});
        Gondor.setNeighbors(new HashSet<>() {{
            add(Oz);
            add(Mordor);
        }});
        Mordor.setNeighbors(new HashSet<>() {{
            add(Gondor);
            add(Oz);
            add(Scadrial);
            add(Hogwarts);
        }});
        Hogwarts.setNeighbors(new HashSet<>() {{
            add(Mordor);
            add(Scadrial);
            add(Roshar);
        }});

        MoveOrder m1 = new MoveOrder(p1, "move", Narnia, Oz, 3);
        MoveOrder m2 = new MoveOrder(p3, "move", Mordor, Hogwarts, 6);
        AttackOrder a1 = new AttackOrder(p1, "attack", Midkemia, Scadrial, 10);
        AttackOrder a2 = new AttackOrder(p2, "attack", Scadrial, Hogwarts, 4);

        OrderBasic[] orders = new OrderBasic[]{m1,m2,a1,a2};

        GameMap map = new GameMap();
        Territory[] territories = new Territory[]{Narnia,Midkemia,Oz,Elantris,Scadrial,Roshar,Gondor,Mordor,Hogwarts};
        map.buildMap(territories);
        //print orderList
        gju.toString(orders);
        gju.print();

        //print gameMap
        gju.toString(map);
        gju.print();

        //print territories
        gju.toString(map.getTerritorySet());
        gju.print();


    }
}
