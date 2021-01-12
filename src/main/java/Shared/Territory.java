package Shared;

import java.util.HashMap;
import java.util.HashSet;

public class Territory {
    private final String territoryName;
    private final String aliasName;
    private HashSet<Territory> neighbors;
    private HashMap<Integer, Integer> territoryUnits;
    private final HashMap<String, Integer> resource;
    private final int size;
    private Player territoryOwner;
    private boolean moveLock;
    private boolean attackLock;

    public Territory(String name, String aliasName, int units, int food, int tech, int size, Player owner) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryUnits = new HashMap<>(){{
            put(0,units);
            for(int level = 1; level <= 6; ++level) {
                put(level, 0);
            }
        }};
        this.resource = new HashMap<>(){{
            put("food",food);
            put("tech",tech);
        }};
        this.size = size;
        this.territoryOwner = owner;
        moveLock = false;
        attackLock = false;
    }

    public Territory(String name, String aliasName, int units, int food, int tech, int size) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryUnits = new HashMap<>(){{
            put(0,units);
            for(int level = 1; level <= 6; ++level) {
                put(level, 0);
            }
        }};
        this.resource = new HashMap<>(){{
            put("food",food);
            put("tech",tech);
        }};
        this.size = size;
        moveLock = false;
        attackLock = false;
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

    public HashMap<Integer, Integer> getUnitsMap() {
        return territoryUnits;
    }

    public int getUnits(int level) {
        return territoryUnits.get(level);
    }

    public int getTotalUnits() {
        int res = 0;
        for(int i = 0; i <= 6; ++i) {
            res += territoryUnits.get(i);
        }
        return res;
    }

    public String getAliasName() { return this.aliasName; }

    public int getSize() {
        return size;
    }

    public Integer getFoodResource() {
        return resource.get("food");
    }

    public Integer getTechResource() {
        return resource.get("tech");
    }

    public boolean isMoveLock() {
        return moveLock;
    }

    public boolean isAttackLock() {
        return attackLock;
    }

    public void setNeighbors(HashSet<Territory> neighbors) {
        this.neighbors = neighbors;
    }

    public void setOwner(Player p) {
        if(territoryOwner == p) return;
        if(territoryOwner != null) {
            territoryOwner.removeTerritory(this);
        }
        territoryOwner = p;
        p.addTerritory(this);
    }

    public void setUnits(int level, int units) {
        if(units < 0) {
            System.out.printf("%s does not have enough units!\n", this.getName());
            throw new IllegalArgumentException();
        }
        territoryUnits.put(level, units);
    }

    public void setUnitsMap(HashMap<Integer,Integer> unitsMap) {
        for(int level = 0; level <= 6; ++level) {
            setUnits(level, unitsMap.get(level));
        }
    }

    public void setMoveLock(boolean moveLock) {
        this.moveLock = moveLock;
    }

    public void setAttackLock(boolean attackLock) {
        this.attackLock = attackLock;
    }

    public void addUnits(int level, int n) {
        if(n < 0) {
            System.out.println("Invalid units!");
            throw new IllegalArgumentException();
        }
        setUnits(level, getUnits(level) + n);
    }

    public void removeUnits(int level, int n) {
        if(n < 0) {
            System.out.println("Invalid units!");
            throw new IllegalArgumentException();
        }
        setUnits(level, getUnits(level) - n);
    }

}
