package Shared;

import java.util.ArrayList;
import java.util.HashMap;

public class GameMap {
    private final HashMap<String, Territory> territoryMap;
    private final HashMap<String, Player> playerMap;
    private int initUnits;

    public GameMap() {
        this.territoryMap = new HashMap<>();
        this.playerMap = new HashMap<>();
        this.initUnits = 0;
    }

    public Territory getTerritoryByName(String aliasName) {
        if(territoryMap.containsKey(aliasName)) {
            return territoryMap.get(aliasName);
        }
        return null;
    }

    public Player getPlayerByName(String name) {
        if(playerMap.containsKey(name)) {
            return playerMap.get(name);
        }
        return null;
    }

    public HashMap<String, Player> getPlayerMap() {
        return this.playerMap;
    }

    public HashMap<String, Territory> getTerritoryMap() {
        return this.territoryMap;
    }

    public void setTerritoryMap(ArrayList<Territory> territoryList) {
        for(Territory territory : territoryList) {
            this.territoryMap.put(territory.getAliasName(),territory);
        }
    }

    public void setPlayerMap(ArrayList<Player> players) {
        for(Player player : players) {
            this.playerMap.put(player.getName(),player);
        }
    }


    public void setInitUnits(int initUnits) {
        this.initUnits = initUnits;
    }

    public int getInitUnits() {
        return this.initUnits;
    }

}
