package Shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class GameMap {
    private HashSet<Territory> territorySet;
    private int initUnits;
    private HashMap<String, Territory> territoryMap;
    private HashMap<String, Player> playerList;

    public GameMap() {
        this.territorySet = new HashSet<>();
        this.territoryMap = new HashMap<String, Territory>();
        this.playerList = new HashMap<String, Player>();
        this.initUnits = 0;
    }

    // maybe not need
    public HashSet<Territory> getTerritorySet() {
        return territorySet;
    }

    public Territory getTerritoryByName(String aliasName) {
        if(territoryMap.containsKey(aliasName)) {
            return territoryMap.get(aliasName);
        }
        return null;
    }

    public Player getPlayerByName(String name) {
        if(playerList.containsKey(name)) {
            return playerList.get(name);
        }
        return null;
    }

    public HashMap<String, Player> getPlayerList() {
        return this.playerList;
    }
    public String getFullName(String aliasName) { return this.territoryMap.get(aliasName).getName(); }

    public HashMap<String, Territory> getTerritoryMap() {
        return this.territoryMap;
    }

    public void buildMap(ArrayList<Territory> territoryList) {
        for(Territory territory : territoryList) {
            this.territorySet.add(territory);
            this.territoryMap.put(territory.getAliasName(),territory);
        }
    }

    public void setPlayers(ArrayList<Player> players) {
        for(Player player : players) {
            this.playerList.put(player.getName(),player);
        }
    }


    public void setInitUnits(int initUnits) {
        this.initUnits = initUnits;
    }

    public int getInitUnits() {
        return this.initUnits;
    }

}
