package Client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class GameMap {
    private HashSet<Territory> territorySet;
    private final int initUnits;

    public GameMap(HashSet<Territory> tS, int initUnits) {
        this.territorySet = tS;
        this.initUnits = initUnits;
    }

    public boolean checkReachable(Territory a, Territory b) {
        String p_name = a.getOwner().getName();
        if(!p_name.equals(b.getOwner().getName())) return false;
        // BFS
        HashSet<Territory> visited = new HashSet<>();
        Queue<Territory> q = new LinkedList<Territory>();
        q.offer(a);
        while(!q.isEmpty()) {
            Territory t = q.poll();
            visited.add(t);
            if (t.getName().equals(b.getName())) return true;
            for(Territory nbT: t.getNeighbors()) {
                if(!visited.contains(nbT) && nbT.getOwner().getName().equals(p_name)) {
                    q.offer(nbT);
                }
            }
        }
        return false;
    }

    public boolean checkAttackable(Territory a, Territory b) {
        // IS attackable when a,b has different owners and a & b are neighbors
        return !a.getOwner().getName().equals(b.getOwner().getName()) && a.getNeighbors().contains(b);
    }
}
