package Shared;

import java.util.HashSet;

public class Territory {
    private final String territoryName;
    private final String aliasName;
    private HashSet<Territory> neighbors;
    private int territoryUnits;
    private Player territoryOwner;
    private boolean moveLock;
    private boolean attackLock;

    public Territory(String name, String aliasName) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        moveLock = false;
        attackLock = false;
    }

    public Territory(String name, String aliasName, Player owner) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryOwner = owner;
        owner.addTerritory(this);
        moveLock = false;
        attackLock = false;
    }

    public Territory(String name, String aliasName, int units, Player owner) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryUnits = units;
        this.territoryOwner = owner;
        if(owner != null) owner.addTerritory(this);
        moveLock = false;
        attackLock = false;
    }

    public Territory(String name, String aliasName, int units) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryUnits = units;
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

    public int getUnits() {
        return territoryUnits;
    }

    public String getAliasName() { return this.aliasName; }

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

    public void setUnits(int units) {
        if(units < 0) {
            System.out.printf("%s does not have enough units!\n", this.getName());
            throw new IllegalArgumentException();
        }
        territoryUnits = units;
    }

    public void setMoveLock(boolean moveLock) {
        this.moveLock = moveLock;
    }

    public void setAttackLock(boolean attackLock) {
        this.attackLock = attackLock;
    }

    public void addUnits(int n) {
        if(n < 0) {
            System.out.println("Invalid units!");
            throw new IllegalArgumentException();
        }
        setUnits(getUnits() + n);
    }

    public void removeUnits(int n) {
        if(n < 0) {
            System.out.println("Invalid units!");
            throw new IllegalArgumentException();
        }
        setUnits(getUnits() - n);
    }

}
