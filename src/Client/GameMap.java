package Client;

import java.util.HashSet;
import java.util.Map;

public class GameMap {
    private HashSet<Territory> territorySet;
    private Map<Territory, HashSet<Territory>> territoryRelation;
    private int initUnits;

    public GameMap() {

    }
}
