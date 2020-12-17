package Utilities;

import Client.*;

import java.util.ArrayList;
import java.util.HashSet;

public class CreateMapJsonStr {
    public static void main(String[] args) {
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
        MoveOrder m1 = new MoveOrder(p1, "move", Narnia, Oz, 3);
        MoveOrder m2 = new MoveOrder(p3, "move", Mordor, Hogwarts, 6);
        AttackOrder a1 = new AttackOrder(p1, "attack", Midkemia, Scadrial, 10);
        AttackOrder a2 = new AttackOrder(p2, "attack", Scadrial, Hogwarts, 4);

        ArrayList<OrderBasic> orders = new ArrayList<OrderBasic>();
        orders.add(m1);
        orders.add(m2);
        orders.add(a1);
        orders.add(a2);

        // build map
        GameMap map = new GameMap();
        Territory[] territories = new Territory[]{Narnia,Midkemia,Oz,Elantris,Scadrial,Roshar,Gondor,Mordor,Hogwarts};
        map.buildMap(territories);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(p1);
        players.add(p2);
        players.add(p3);

        // create territoryGroup
        // group 1
        ArrayList<Territory> t1 = new ArrayList<Territory>();;
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
        gjU.writeMapToJson(map, territoryGroup);
    }
}
