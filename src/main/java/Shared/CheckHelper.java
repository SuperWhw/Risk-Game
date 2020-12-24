package Shared;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class CheckHelper {

    public boolean checkName(String s) {
        if(s == null) {
            System.out.println("Empty name!");
            return false;
        }
        if(s.matches("[a-zA-Z]+")) return true;
        else {
            System.out.println("Name should only contain letters!");
            return false;
        }
    }

    public boolean checkInitUnitsList(String s, int totalUnits, int territoryNum) {
        String[] strArray = s.split(" ");
        if(strArray.length != territoryNum) {
            System.out.printf("Argument length is not equal to %d!\n",territoryNum);
            return false;
        }
        try {
            int sum = 0;
            for(var str: strArray) {
                int unit = Integer.parseInt(str);
                if(unit < 0) {
                    System.out.println("Unit should be positive!");
                    return false;
                }
                sum += unit;
            }
            if(sum == totalUnits) return true;
            else {
                System.out.printf("Total units are not equal to %d!\n", totalUnits);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
        return false;
    }

    public boolean checkOrderBasic(OrderBasic order) {
        if(!order.getPlayer().getName().equals(order.getFromT().getOwner().getName())) {
            System.out.printf("%s does not belong to you!\n", order.getFromT().getName());
            return false;
        }
        if(order.getOrderType().equals("move")) {
            if(order.getFromT().isMoveLock()) {
                System.out.printf("%s cannot move now!\n", order.getFromT().getName());
                return false;
            }
            if(!checkReachable(order.getFromT(), order.getToT()) || order.getToT().isAttackLock()) {
                System.out.printf("%s cannot move to %s!\n", order.getFromT().getName(), order.getToT().getName());
                return false;
            }
        }
        else if(order.getOrderType().equals("attack")) {
            if(order.getFromT().isAttackLock()) {
                System.out.printf("%s cannot attack now!\n", order.getFromT().getName());
                return false;
            }
            if(!checkAttackable(order.getFromT(), order.getToT())) {
                System.out.printf("%s cannot attack %s!\n", order.getFromT().getName(), order.getToT().getName());
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
