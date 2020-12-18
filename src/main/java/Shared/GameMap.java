package Shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class GameMap {
    private HashSet<Territory> territorySet;
    private int initUnits;
    private HashMap<String, Territory> territoryMap;
    public GameMap() {
        this.territorySet = new HashSet<>();
        this.territoryMap = new HashMap<String, Territory>();
        this.initUnits = 0;
    }

    // maybe not need
    public HashSet<Territory> getTerritorySet() {
        return territorySet;
    }

    public Territory getTerritoryByName(String string) {
        return this.territoryMap.get(string);
    }

    public HashMap<String, Territory> getTerritoryMap() {
        return this.territoryMap;
    }
    public void buildMap(Territory[] territoryList) {
        Collections.addAll(territorySet, territoryList);
        this.territoryMap = new HashMap<String, Territory>();
        for(Territory territory : territoryList) {
            this.territoryMap.put(territory.getName(),territory);
        }
    }

    public void buildMap(ArrayList<Territory> territoryList) {
        this.territoryMap = new HashMap<String, Territory>();
        for(Territory territory : territoryList) {
            this.territorySet.add(territory);
            this.territoryMap.put(territory.getName(),territory);
        }
    }

    public void setInitUnits(int initUnits) {
        this.initUnits = initUnits;
    }

    public int getInitUnits() {
        return this.initUnits;
    }

}
