package Utilities;
import Client.*;
import com.google.gson.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class GameJsonUtils {

    class PlayerJsonAdaptor {
        public String name;
        public ArrayList<String> territories;
        public PlayerJsonAdaptor(Player player) {
            this.name = player.getName();
            for(Territory territory : player.getTerritories()) {
                this.territories.add(territory.getName());
            }
        }
        private PlayerJsonAdaptor() { }
        public void print() {
            System.out.println("Player\nname : " + this.name + '\n' + "territories: " + this.territories);
        }
    }

    class TerritoryJsonAdaptor {
        public String name;
        public String owner;
        public int units;
        public ArrayList<String> neighbors;
        public void print() {
            System.out.println("Territory\nname : " + this.name + '\n' + "neighbors: " + this.neighbors + '\n'
                    + "owner: " + this.owner + "\nunits: " + this.units);
        }
        private TerritoryJsonAdaptor() { }
        public TerritoryJsonAdaptor(Territory territory) {
            this.neighbors = new ArrayList<String>();
            this.owner = territory.getOwner().getName();
            this.name = territory.getName();
            this.units = territory.getUnits();
            for(Territory Territory : territory.getNeighbors()) {
                this.neighbors.add(Territory.getName());
            }
        }
    }

    class GameMapJsonAdaptor {
        public ArrayList<PlayerJsonAdaptor> players;
        public ArrayList<TerritoryJsonAdaptor> territorySet;
        public int initUnits;
        public void print() {
            System.out.println("GameMap:\nInitUnit: " + this.initUnits);
            for(PlayerJsonAdaptor t : this.players) {
                t.print();
            }
            for(TerritoryJsonAdaptor t : this.territorySet) {
                t.print();
            }
        }
        private GameMapJsonAdaptor() { }
        public GameMapJsonAdaptor(GameMap gameMap, ArrayList<Player> players) {
            this.players = new ArrayList<PlayerJsonAdaptor>();
            this.territorySet = new ArrayList<TerritoryJsonAdaptor>();
            this.initUnits = gameMap.getInitUnits();
            for(Territory territory : gameMap.getTerritorySet()) {
                this.territorySet.add(new TerritoryJsonAdaptor(territory));
            }
            for(Player player : players) {
                this.players.add(new PlayerJsonAdaptor(player));
            }
        }
    }
    public void writeToJson(GameMap gameMap, ArrayList<Player> players) {

        GameMapJsonAdaptor adaptor =  new GameMapJsonAdaptor(gameMap, players);
        adaptor.print();

    }
    public void writeToJson(Player player) {
        PlayerJsonAdaptor adaptor = new PlayerJsonAdaptor(player);
        adaptor.print();
    }

    public void writeToJson(Territory territory) {
        TerritoryJsonAdaptor adaptor = new TerritoryJsonAdaptor(territory);
        adaptor.print();
    }

    public static void main(String[] argv) {
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

        ArrayList<OrderBasic> orders = new ArrayList<OrderBasic>();
        orders.add(m1);
        orders.add(m2);
        orders.add(a1);
        orders.add(a2);

        GameMap map = new GameMap();
        Territory[] territories = new Territory[]{Narnia,Midkemia,Oz,Elantris,Scadrial,Roshar,Gondor,Mordor,Hogwarts};
        map.buildMap(territories);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        GameJsonUtils gjU = new GameJsonUtils();
        gjU.writeToJson(map, players);
    }
}