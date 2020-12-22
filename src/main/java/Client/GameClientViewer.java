package Client;

import Shared.GameMap;
import Shared.Player;
import Shared.Territory;

public class GameClientViewer {
    public void printMap(GameMap map, Player player, String style) {
        // always print own territories first
        System.out.printf("%s player: (You)\n", player.getName());
        System.out.print("-------------\n");
        printOwnedTerritory(player, style);

        for(var p: map.getPlayerMap().values()) if(p != player) {
            System.out.printf("%s player: \n", p.getName());
            System.out.print("-------------\n");
            printOwnedTerritory(p, style);
        }
    }

    private void printOwnedTerritory(Player p, String style) {
        if(style.equals("simple")) {
            for (var t : p.getTerritories()) {
                System.out.printf("%s (next to:", t.getName());
                for (var tn : t.getNeighbors()) {
                    System.out.printf(" %s", tn.getName());
                }
                System.out.print(")\n");
            }
            System.out.println();
        }
        if(style.equals("order")) {
            for (var t : p.getTerritories()) {
                String color = "white";
                if(t.isAttackLock()) {
                    color = "red";
                    System.out.print("|" + colorStr("A",color) + "|" +colorStr("M",color) + "|");
                }
                else if(t.isMoveLock()) {
                    color = "cyan";
                    System.out.print("|" +colorStr("M",color) + "|");
                }
                System.out.print(colorStr(getTerritoryOrderName(t),color) + "[" + t.getUnits() + "] (next to:");
                for (var tn : t.getNeighbors()) {
                    System.out.printf(" %s[%d]", getTerritoryOrderName(tn), tn.getUnits());
                }
                System.out.print(")\n");
            }
            System.out.println();
        }
    }

    private String getTerritoryOrderName(Territory t) {
        int lenAlias = t.getAliasName().length();
        return "(" + t.getAliasName() + ")" + t.getName().substring(lenAlias);
    }

    private String colorStr(String s, String color) {
        if(color.equals("cyan")) {
            return "\033[36m" + s + "\033[0m";
        }
        else if(color.equals("red")) {
            return "\033[31m" + s + "\033[0m";
        }
        else {
            return s;
        }
    }
}
