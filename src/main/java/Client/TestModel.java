package Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class TestModel {
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

        // build map
        GameMap map = new GameMap();
        map.buildMap(new Territory[]{Narnia,Midkemia,Oz,Elantris,Scadrial,Roshar,Gondor,Mordor,Hogwarts});

        // create action
        MoveOrder m1 = new MoveOrder(p1, "move", Narnia, Oz, 3);
        MoveOrder m2 = new MoveOrder(p3, "move", Mordor, Hogwarts, 6);
        AttackOrder a1 = new AttackOrder(p1, "attack", Midkemia, Scadrial, 10);
        AttackOrder a2 = new AttackOrder(p2, "attack", Scadrial, Hogwarts, 4);

        ArrayList<OrderBasic> orderList = new ArrayList<>(Arrays.asList(m1,m2,a1,a2));

        // check order
        CheckHelper checker = new CheckHelper();
        for(var order: orderList) {
            System.out.print(checker.checkOrderBasic(order)+" ");
            System.out.println();
        }

        // take action

        OrderHandler orders = new OrderHandler(map);
        map = orders.execute(orderList);

        // print current state
        for(Territory t: map.getTerritorySet()) {
            System.out.printf("Territory name: %s\n\tOwner:%s\tUnits: %d\n",t.getName(),t.getOwner().getName(),t.getUnits());
        }

    }
}