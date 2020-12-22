package Utilities;

import Shared.*;

import java.util.ArrayList;
import java.util.HashSet;

public class CreateMapJsonStr {
    public static void main(String[] args) {
        // create players
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");

        // create territories
        Territory Narnia = new Territory("Narnia", "N",0, p1);
        Territory Midkemia = new Territory("Midkemia", "Mi",0, p1);
        Territory Oz = new Territory("Oz", "O", 0, p1);
        Territory Elantris = new Territory("Elantris", "E",0, p2);
        Territory Scadrial = new Territory("Scadrial", "S",0, p2);
        Territory Roshar = new Territory("Roshar", "R",0, p2);
        Territory Gondor = new Territory("Gondor", "G",0, p3);
        Territory Mordor = new Territory("Mordor", "Mo",0, p3);
        Territory Hogwarts = new Territory("Hogwarts", "H",0, p3);

        // set neighbors
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

        // set orders
        MoveOrder m1 = new MoveOrder(p1, "MoveOrder", Narnia, Oz, 3);
        MoveOrder m2 = new MoveOrder(p3, "MoveOrder", Mordor, Hogwarts, 6);
        AttackOrder a1 = new AttackOrder(p1, "AttackOrder", Midkemia, Scadrial, 10);
        AttackOrder a2 = new AttackOrder(p2, "AttackOrder", Scadrial, Hogwarts, 4);

        ArrayList<OrderBasic> orders = new ArrayList<OrderBasic>();
        orders.add(m1);
        orders.add(m2);
        orders.add(a1);
        orders.add(a2);

        // build map
        GameMap map = new GameMap();
        map.setTerritoryMap(new ArrayList<>(){{
            add(Narnia);
            add(Midkemia);
            add(Oz);
            add(Elantris);
            add(Scadrial);
            add(Roshar);
            add(Gondor);
            add(Mordor);
            add(Hogwarts);
        }});
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(p1);
        players.add(p2);
        players.add(p3);

        // create territoryGroup
        // group 1
        ArrayList<Territory> t1 = new ArrayList<Territory>();
        t1.add(Narnia);
        t1.add(Midkemia);
        t1.add(Oz);

        // group 2
        ArrayList<Territory> t2 = new ArrayList<Territory>();
        t2.add(Gondor);
        t2.add(Mordor);
        t2.add(Hogwarts);

        // group 3
        ArrayList<Territory> t3 = new ArrayList<Territory>();;
        t3.add(Scadrial);
        t3.add(Roshar);
        t3.add(Elantris);

        // combine group 1-3
        ArrayList<ArrayList<Territory> > territoryGroup = new ArrayList<ArrayList<Territory> >();
        territoryGroup.add(t1);
        territoryGroup.add(t2);
        territoryGroup.add(t3);

        GameJsonUtils gjU = new GameJsonUtils();
        String mapJson = gjU.init_writeMapToJson(map, territoryGroup);
        FileIOBasics fileIO = new FileIOBasics();
        fileIO.writeFile("map.json", mapJson);
    }
}
