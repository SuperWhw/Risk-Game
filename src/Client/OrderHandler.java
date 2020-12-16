package Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class OrderHandler {
    GameMap map;

    public OrderHandler(GameMap map) {
        this.map = map;
    }

    private ArrayList<AttackOrder> mergeAttack(ArrayList<AttackOrder> attackList) {
        ArrayList<AttackOrder> res = new ArrayList<>();
        HashMap<Territory, HashMap<Player, Integer>> attHandler = new HashMap<>();
        for(var att: attackList) {
            Territory toT = att.getToT();
            if(attHandler.containsKey(toT)) {
                Player p = att.getPlayer();
                if(attHandler.get(toT).containsKey(p)) {
                    int v = attHandler.get(toT).get(p);
                    attHandler.get(toT).replace(p, v+att.getUnits());
                }
                else {
                    attHandler.get(toT).put(p, att.getUnits());
                }
            }
            else {
                attHandler.put(toT, new HashMap<Player, Integer>());
                attHandler.get(toT).put(att.getPlayer(), att.getUnits());
            }
        }

        for(var t: attHandler.keySet()) {
            var playerUnitsMap = attHandler.get(t);
            // Random player list

            for(var p: playerUnitsMap.keySet()) {
                res.add(new AttackOrder(p, "attack", t, t, playerUnitsMap.get(p)));
            }
        }
        return res;
    }

    public GameMap execute(ArrayList<OrderBasic> orderList) {

        ArrayList<MoveOrder> moveList = new ArrayList<>();
        ArrayList<AttackOrder> attackList = new ArrayList<>();

        // group orders
        for(var order: orderList) {
            if(order.getOrderType().equals("move")) {
                moveList.add((MoveOrder) order);
            }
            else if(order.getOrderType().equals("attack")) {
                // Attackers should leave out of their owner Territory first.
                moveList.add(new MoveOrder(order.getPlayer(), "move", order.getFromT(), order.getToT(), order.getUnits()));
                attackList.add((AttackOrder) order);
            }
        }

        attackList = mergeAttack(attackList);

        // execute move and 1st step of attack
        for(var order: moveList) order.execute();


    }
}
