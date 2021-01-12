package Shared;

import java.util.HashSet;

public class Player {
    private String playerName;
    private HashSet<Territory> territories;
    private int techLevel;
    private int foodTotal;
    private int techTotal;

    public Player(String playerName) {
        this.playerName = playerName;
        this.territories = new HashSet<>();
        this.techLevel = 1;
        this.foodTotal = 0;
        this.techTotal = 0;
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
            System.out.println("Cannot remove territory!");
        }
    }

    public int getTechLevel() {
        return techLevel;
    }

    public void setTechLevel(int level) {
        if(level > 6 || level < 0) throw new IllegalArgumentException();
        techLevel = level;
    }

    public int getFoodTotal() {
        return foodTotal;
    }

    public void refreshFoodTotal() {
        for(var t: territories) {
            this.foodTotal += t.getFoodResource();
        }
    }

    public int getTechTotal() {
        return techTotal;
    }

    public void refreshTechTotal() {
        for(var t: territories) {
            this.techTotal += t.getTechResource();
        }
    }

}
