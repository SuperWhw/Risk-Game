package Utilities;

import Shared.*;

public class GameStringUtils {
    public OrderBasic strToOrder(GameMap map, String str, Player p) {
        String[] strArray = str.split(" ");
        if(strArray.length != 4) {
            System.out.println("Argument length is not equal to 4!");
            throw new IllegalArgumentException();
        }

        String orderType = strArray[0];
        Territory fromT = map.getTerritoryByName(strArray[1]);
        Territory toT = map.getTerritoryByName(strArray[2]);
        int units = Integer.parseInt(strArray[3]);

        if(fromT == null || toT == null) {
            System.out.println("Invalid territory name!");
            throw new IllegalArgumentException();
        }

        if(orderType.equals("M")) {
            orderType = "move";
            return new MoveOrder(p, orderType, fromT, toT, units);
        }
        else if(orderType.equals("A")) {
            orderType = "attack";
            return new AttackOrder(p, orderType, fromT, toT, units);
        }
        else {
            System.out.println("Order type is not supported!");
            throw new IllegalArgumentException();
        }

    }

}
