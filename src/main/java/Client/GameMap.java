package Client;

import java.util.*;

public class GameMap {
    private HashSet<Territory> territorySet;
    private int initUnits;

    public GameMap() {
        this.territorySet = new HashSet<>();
    }


    // maybe not need
    public HashSet<Territory> getTerritorySet() {
        return territorySet;
    }

    public void buildMap(Territory[] territoryList) {
        Collections.addAll(territorySet, territoryList);
    }

    public void setInitUnits(int initUnits) {
        this.initUnits = initUnits;
    }

    public int getInitUnits() { return this.initUnits; }

}
