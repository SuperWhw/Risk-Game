package Client;

import Client.Player;

import java.util.HashSet;

public class Territory {
    private final String territoryName;
    private Player territoryOwner;
    private final HashSet<Territory> neighbors;
    private int territoryUnits;

    public Territory(String name, Player owner, HashSet<Territory> neighbors, int units) {
        this.territoryName = name;
        this.territoryOwner = owner;
        this.neighbors = neighbors;
        this.territoryUnits = units;
    }

    public String getName() {
        return territoryName;
    }

    public Player getOwner() {
        return territoryOwner;
    }

    public HashSet<Territory> getNeighbors() {
        return neighbors;
    }

    public int getUnits() {
        return territoryUnits;
    }

    public void setOwner(Player p) {
        territoryOwner = p;
    }

    public void setUnits(int units) {
        territoryUnits = units;
    }
}
