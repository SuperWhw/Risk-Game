package Client;

import Shared.GameMap;
import Shared.Player;
import Shared.Territory;
import Utilities.ColorPrint;

public class GameClientViewer {
    public void printMap(GameMap map, Player player, String style) {
        for(var p: map.getPlayerMap().values()) {
            if(p.getName().equals(player.getName())) {
                System.out.printf("%s player: (You)\n", player.getName());
            }
            else {
                System.out.printf("%s player: \n", p.getName());
            }
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
            ColorPrint cp = new ColorPrint();
            for (var t : p.getTerritories()) {
                ColorPrint.Color color = ColorPrint.Color.WHITE;
                if(t.isAttackLock()) {
                    color = ColorPrint.Color.RED;
                    System.out.print("|" + cp.setColor("A", color) + "|" + cp.setColor("M",color) + "|");
                }
                else if(t.isMoveLock()) {
                    color = ColorPrint.Color.SKYBLUE;
                    System.out.print("|" +cp.setColor("M",color) + "|");
                }
                System.out.print(cp.setColor(getTerritoryOrderName(t),color) + "[" + t.getUnits() + "] (next to:");
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
}
