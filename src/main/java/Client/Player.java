package Client;

import java.util.HashSet;

public class Player {
    private String playerName;
    private HashSet<Territory> territories;
    private LinkInfo link_info;

    public Player(String playerName) {
        this.playerName = playerName;
        this.territories = new HashSet<Territory>();
    }

    public String getName() {
        return playerName;
    }

    public HashSet<Territory> getTerritories() {
        return territories;
    }

    public void addTerritory(Territory t) {
        territories.add(t);
    }

    public void removeTerritory(Territory t) {
        try {
            territories.remove(t);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

}
