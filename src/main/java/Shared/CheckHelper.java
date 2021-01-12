package Shared;

import java.util.*;

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

    public boolean checkOrder(Order order) {
        if(order.getOrderType().equals("move") || order.getOrderType().equals("attack")) {
            return checkOrderBasic((OrderBasic) order);
        }
        else if(order.getOrderType().equals("upgrade")) {
            return checkUpgradeOrder((UpgradeOrder) order);
        }
        System.out.println("Invalid order type! (Should be \"move\" or \"attack\" or \"upgrade\")");
        return false;
    }

    public boolean checkUpgradeOrder(UpgradeOrder order) {
        UpgradeHelper uh = new UpgradeHelper();
        if(!order.getPlayer().getName().equals(order.getTerritory().getName())) {
            System.out.printf("%s does not belong to you!\n", order.getTerritory().getName());
            return false;
        }
        if(order.getTerritory().isAttackLock()) {
            System.out.printf("%s cannot upgrade, because it is attack locked!\n", order.getTerritory().getName());
            return false;
        }

        // upgrade tech level
        if(order.getTerritory() == null) {
            if(order.getPlayer().getTechLevel() == 6) {
                System.out.println("Could not upgrade tech level, because it is at max level!");
                return false;
            }
            if(order.getPlayer().getTechTotal() < uh.techUpgrade[order.getPlayer().getTechLevel()]) {
                System.out.printf("%s cannot upgrade tech now, because tech is not enough!\n", order.getPlayer().getName());
                return false;
            }
        }
        else { // upgrade units
            if(order.getFromLevel() > 6 || order.getFromLevel() < 0 || order.getToLevel() > 6 || order.getToLevel() < 0) {
                System.out.println("Level should be in range 0-6!");
                return false;
            }
            if(order.getFromLevel() >= order.getToLevel()) {
                System.out.println("Upgrade level should larger than original!");
                return false;
            }
            if(order.getPlayer().getTechLevel() < order.getToLevel()) {
                System.out.printf("Units cannot upgrade to %d, because %s's tech level is %d!\n", order.getToLevel(), order.getPlayer().getName(), order.getPlayer().getTechLevel());
                return false;
            }
            int requiredTech = uh.presumUnitsUpgrade[order.getToLevel()] - uh.presumUnitsUpgrade[order.getFromLevel()];
            if(order.getPlayer().getTechTotal() < requiredTech) {
                System.out.printf("%s cannot upgrade units now, because tech is not enough! ", order.getPlayer().getName());
                System.out.printf("Have: %d, Required: %d\n", order.getPlayer().getTechTotal(), requiredTech);
                return false;
            }
        }
        return true;
    }

    public boolean checkOrderBasic(OrderBasic order) {
        if(!order.getPlayer().getName().equals(order.getFromT().getOwner().getName())) {
            System.out.printf("%s does not belong to you!\n", order.getFromT().getName());
            return false;
        }
        if(order.getOrderType().equals("move")) {
            if(order.getFromT().isMoveLock()) {
                System.out.printf("%s cannot move now, because it is move locked!\n", order.getFromT().getName());
                return false;
            }
            if(!order.getPlayer().getName().equals(order.getToT().getOwner().getName())) {
                System.out.printf("%s does not belong to you!\n", order.getToT().getName());
                return false;
            }
            if(order.getToT().isAttackLock()) {
                System.out.printf("%s cannot move to %s, because %s is attack locked!\n", order.getFromT().getName(), order.getToT().getName(), order.getToT().getName());
                return false;
            }
            int dist = checkReachable(order.getFromT(), order.getToT());
            if(dist == -1) {
                System.out.printf("%s cannot move to %s, because they are not reachable!\n", order.getFromT().getName(), order.getToT().getName());
                return false;
            }
            else if(dist * order.getUnits() > order.getPlayer().getFoodTotal()) {
                System.out.printf("%s cannot move now, because food is not enough! ", order.getFromT().getName());
                System.out.printf("Have: %d, Required: %d\n", order.getPlayer().getFoodTotal(), dist * order.getUnits());
                return false;
            }
        }
        else if(order.getOrderType().equals("attack")) {
            if(order.getFromT().isAttackLock()) {
                System.out.printf("%s cannot attack now, because is attack locked!\n", order.getFromT().getName());
                return false;
            }
            if(!checkAttackable(order.getFromT(), order.getToT())) {
                System.out.printf("%s cannot attack %s!\n", order.getFromT().getName(), order.getToT().getName());
                return false;
            }
            if(order.getUnits() > order.getPlayer().getFoodTotal()) {
                System.out.printf("%s cannot attack now, because food is not enough!\n", order.getFromT().getName());
                return false;
            }
        }
        return true;
    }

    public int checkReachable(Territory a, Territory b) {
        //Dijkstra
        Queue<Territory> q = new PriorityQueue<>();
        HashMap<String, Integer> dist = new HashMap<>();
        HashSet<String> done = new HashSet<>();
        dist.put(a.getName(),0);
        q.offer(a);
        while(!q.isEmpty()) {
            Territory x = q.poll();
            if(done.contains(x.getName())) continue;
            done.add(x.getName());
            if(x.getName().equals(b.getName())) return dist.get(b.getName());
            for(var t: x.getNeighbors()) {
                if(dist.getOrDefault(t.getName(),Integer.MAX_VALUE) > dist.get(x.getName()) + t.getSize()) {
                    dist.put(t.getName(),dist.get(x.getName()) + t.getSize());
                    q.offer(t);
                }
            }
        }
        return -1;
    }

    public boolean checkAttackable(Territory a, Territory b) {
        // IS attackable when a,b has different owners and a & b are neighbors
        return !a.getOwner().getName().equals(b.getOwner().getName()) && a.getNeighbors().contains(b);
    }
}
