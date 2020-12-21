package Client;

import Shared.*;
import Utilities.FileIOBasics;
import Utilities.GameJsonUtils;
import Utilities.GameStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;


public class TestClientModel {
    public static void main(String[] args) {

        // create players
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");

        // create territories
        Territory Narnia = new Territory("Narnia", "N",10, p1);
        Territory Midkemia = new Territory("Midkemia", "Mi",12, p1);
        Territory Oz = new Territory("Oz", "O", 8, p1);
        Territory Elantris = new Territory("Elantris", "E",6, p2);
        Territory Scadrial = new Territory("Scadrial", "S",5, p2);
        Territory Roshar = new Territory("Roshar", "R",3, p2);
        Territory Gondor = new Territory("Gondor", "G",13, p3);
        Territory Mordor = new Territory("Mordor", "Mo",14, p3);
        Territory Hogwarts = new Territory("Hogwarts", "H",3, p3);

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

        map.buildMap(new ArrayList<>(){{
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

        // print map
        GameClientViewer viewer = new GameClientViewer();
        viewer.printMap(map, p1, "simple");

        // input order loop, "commit" for break
        // print gameMap
        Scanner scanner = new Scanner(System.in);
        String in;
        GameStringUtils gsu = new GameStringUtils();
        OrderHandler handler = new OrderHandler();

        while(true) {
            try {
                viewer.printMap(map, p1, "order");
                System.out.println("Please input order: ");
                in = scanner.nextLine();
                if(in.equals("commit")) break;
                OrderBasic order = gsu.strToOrder(map, in, p1);

                handler.execute(map, order);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format!");
            } catch (Exception e) {
                System.out.println();
            }
            System.out.println();
        }

    }
}