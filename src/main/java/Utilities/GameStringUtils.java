package Utilities;

import Shared.*;

public class GameStringUtils {
    public Order strToOrder(GameMap map, String str, Player p) {
        String[] strArray = str.split(" ");
        String orderType = strArray[0];

        // parse upgrade order
        if(orderType.equals("U")) {
            orderType = "upgrade";
            if(strArray.length == 1) {
                return new UpgradeOrder(p, orderType);
            }
            else if(strArray.length == 5) {
                Territory t = map.getTerritoryByName(strArray[1]);
                int fromLevel = Integer.parseInt(strArray[2]);
                int toLevel = Integer.parseInt(strArray[3]);
                int units = Integer.parseInt(strArray[4]);

                if(t == null) {
                    System.out.println("Invalid territory name!");
                    throw new IllegalArgumentException();
                }

                return new UpgradeOrder(p, orderType, t, fromLevel, toLevel, units);
            }
            else {
                System.out.println("Argument length is not correct!");
                throw new IllegalArgumentException();
            }
        }

        // parse move, attack order
        if(strArray.length != 5) {
            System.out.println("Argument length is not equal to 4!");
            throw new IllegalArgumentException();
        }

        Territory fromT = map.getTerritoryByName(strArray[1]);
        Territory toT = map.getTerritoryByName(strArray[2]);
        int level = Integer.parseInt(strArray[3]);
        int units = Integer.parseInt(strArray[4]);

        if(fromT == null || toT == null) {
            System.out.println("Invalid territory name!");
            throw new IllegalArgumentException();
        }

        if(orderType.equals("M")) {
            orderType = "move";
            return new MoveOrder(p, orderType, fromT, toT, level, units);
        }
        else if(orderType.equals("A")) {
            orderType = "attack";
            return new AttackOrder(p, orderType, fromT, toT, level, units);
        }
        else {
            System.out.println("Order type is not supported!");
            throw new IllegalArgumentException();
        }

    }

}
