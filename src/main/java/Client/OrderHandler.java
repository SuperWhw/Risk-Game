package Client;

import Shared.*;

public class OrderHandler {

    public void execute(OrderBasic order) {
        CheckHelper checker = new CheckHelper();

        if(!checker.checkOrderBasic(order)) {
            throw new IllegalArgumentException();
        }

        // if order is "move", first execute it, then lock "moveLock" of the fromT territory
        // if order is "attack", first execute half of it, then lock the "moveLock" and "attackLock" of the fromT territory
        if (order.getOrderType().equals("move")) {
            order.execute();
            order.getFromT().setMoveLock(true);
        } else if (order.getOrderType().equals("attack")) {
            // Attackers should leave out of their owner Territory first.
            MoveOrder m = new MoveOrder(order.getPlayer(), "move", order.getFromT(), null, order.getUnits());
            m.execute();
            order.getFromT().setMoveLock(true);
            order.getFromT().setAttackLock(true);
        }
    }

}

