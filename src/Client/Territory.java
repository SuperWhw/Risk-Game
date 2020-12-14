package Client;

import Client.Player;

public class Territory {
    private String territoryName;
    private Player territoryOwner;
    private int territoryUnits;

    public Territory(String name, Player owner, int units) {
        this.territoryName = name;
        this.territoryOwner = owner;
        this.territoryUnits = units;
    }

    public String getName() {
        return territoryName;
    }

    public Player getOwner() {
        return territoryOwner;
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
