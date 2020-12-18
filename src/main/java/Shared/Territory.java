package Shared;

import java.util.HashSet;

public class Territory {
    private final String territoryName;
    private final String aliasName;
    private HashSet<Territory> neighbors;
    private int territoryUnits;
    private Player territoryOwner;

    public Territory(String name, String aliasName) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
    }

    public Territory(String name, String aliasName, Player owner) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryOwner = owner;
    }

    public Territory(String name, String aliasName, int units, Player owner) {
        this.territoryName = name;
        this.aliasName = aliasName;
        this.neighbors = new HashSet<>();
        this.territoryUnits = units;
        this.territoryOwner = owner;
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

    public void setNeighbors(HashSet<Territory> neighbors) {
        this.neighbors = neighbors;
    }

    public void setOwner(Player p) {
        territoryOwner = p;
    }

    public void setUnits(int units) {
        if(units < 0) {
            throw new IllegalArgumentException();
        }
        else {
            territoryUnits = units;
        }
    }

    public void addUnits(int n) {
        try{
            if(n < 0) {
                throw new IllegalArgumentException();
            }
            setUnits(getUnits() + n);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Illegal Argument: Please input valid units!");
        }
        catch (Exception e) {
            System.out.println("Unknown Error!");
        }
    }

    public void removeUnits(int n) {
        try{
            if(n < 0) {
                throw new IllegalArgumentException();
            }
            setUnits(getUnits() - n);
        }
        catch (IllegalArgumentException e) {
            System.out.printf("Territory %s doesn't have enough units!\n", getName());
        }
        catch (Exception e) {
            System.out.println("Unknown Error!");
        }
    }

}
