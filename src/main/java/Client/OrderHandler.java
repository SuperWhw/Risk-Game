package Client;

import Shared.*;

public class OrderHandler {

    public void execute(Order order) {
        CheckHelper checker = new CheckHelper();

        if(!checker.checkOrder(order)) {
            throw new IllegalArgumentException();
        }

        // if order is "move", first execute it, then lock "moveLock" of the fromT territory
        // if order is "attack", first execute half of it, then lock the "moveLock" and "attackLock" of the fromT territory
        if(order.getOrderType().equals("upgrade")) {
            UpgradeOrder upgradeOrder = (UpgradeOrder) order;
            if(upgradeOrder.getTerritory() != null) {
                upgradeOrder.execute();
            }
        }
        else if(order.getOrderType().equals("move")) {
            MoveOrder moveOrder = (MoveOrder) order;
            moveOrder.execute();
            moveOrder.getFromT().setMoveLock(true);
        }
        else if(order.getOrderType().equals("attack")) {
            // Attackers should leave out of their owner Territory first.
            AttackOrder attackOrder = (AttackOrder) order;
            MoveOrder m = new MoveOrder(order.getPlayer(), "move", attackOrder.getFromT(), null, attackOrder.getLevel(), attackOrder.getUnits());
            m.execute();
            attackOrder.getFromT().setMoveLock(true);
            attackOrder.getFromT().setAttackLock(true);
        }
    }

}

