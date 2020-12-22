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

        for(var p: map.getPlayerSet()) if(p != player) {
            System.out.printf("%s player: \n", player.getName());
            System.out.print("-------------\n");
            printOwnedTerritory(player, style);
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
                if(t.isAttackLock()) {
                    System.out.print("|A|M|");
                }
                else if(t.isMoveLock()) {
                    System.out.print("|M|");
                }
                System.out.printf("%s[%d] (next to:", getTerritoryOrderName(t), t.getUnits());
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
