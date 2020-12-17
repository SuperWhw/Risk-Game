package Client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class CheckHelper {

    public boolean checkOrderBasic(OrderBasic order) {
        if(!order.getPlayer().getName().equals(order.getFromT().getOwner().getName())) {
            System.out.printf("%s does't belong to you!\n", order.getFromT().getName());
            return false;
        }
        if(order.getOrderType().equals("move")) {
            if(!checkReachable(order.getFromT(), order.getToT())) {
                System.out.printf("%s cannot move to %s!\n", order.getFromT().getName(), order.getToT().getName());
                return false;
            }
        }
        else if(order.getOrderType().equals("attack")) {
            if(!checkAttackable(order.getFromT(), order.getToT())) {
                System.out.printf("%s cannot reach %s!\n", order.getFromT().getName(), order.getToT().getName());
                return false;
            }
        }
        else {
            System.out.println("Invalid order type! (Should be \"move\" or \"attack\")");
            return false;
        }
        return true;
    }

    public boolean checkReachable(Territory a, Territory b) {
        String p_name = a.getOwner().getName();
        if(!p_name.equals(b.getOwner().getName())) return false;
        // BFS
        HashSet<Territory> visited = new HashSet<>();
        Queue<Territory> q = new LinkedList<>();
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
