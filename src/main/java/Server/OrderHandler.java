package Server;

import Shared.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class OrderHandler {

    public void execute(GameMap map, ArrayList<OrderBasic> orderList) {

        ArrayList<MoveOrder> moveList = new ArrayList<>();
        ArrayList<AttackOrder> attackList = new ArrayList<>();
        CheckHelper checker = new CheckHelper();

        // group orders
        for(var order: orderList) {
            if(!checker.checkOrderBasic(order)) continue;

            if(order.getOrderType().equals("move")) {
                moveList.add((MoveOrder) order);
            }
            else if(order.getOrderType().equals("attack")) {
                // Attackers should leave out of their owner Territory first.
                moveList.add(new MoveOrder(order.getPlayer(), "move", order.getFromT(), null, order.getUnits()));
                attackList.add((AttackOrder) order);
            }
        }

        attackList = mergeAttack(attackList);

        // execute move and 1st step of attack
        for(var order: moveList) order.execute();

        //execute attack;
        for(var order: attackList) order.execute();

    }

    private ArrayList<AttackOrder> mergeAttack(ArrayList<AttackOrder> attackList) {
        ArrayList<AttackOrder> res = new ArrayList<>();

        // merge force if attackers attack same territory who are from same player
        HashMap<Territory, HashMap<Player, Integer>> attHandler = mergeAttackForce(attackList);

        for(var t: attHandler.keySet()) {
            // get random player list
            Player[] playerArray = getRandomPlayerArray(attHandler.get(t).keySet().toArray(new Player[0]));

            for(var p: playerArray) {
                // we don't need fromT, so the parameter is dummy
                res.add(new AttackOrder(p, "attack", t, t, attHandler.get(t).get(p)));
            }
        }
        return res;
    }

    private HashMap<Territory, HashMap<Player, Integer>> mergeAttackForce(ArrayList<AttackOrder> attackList) {
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
        return attHandler;
    }

    private Player[] getRandomPlayerArray(Player[] playerArray) {
        int n = playerArray.length;
        if(n == 1) return playerArray;

        Random rand = new Random();
        for(int i = 0; i < n; ++i) {
            int randomIndexToSwap = rand.nextInt(n);
            Player tmp = playerArray[randomIndexToSwap];
            playerArray[randomIndexToSwap] = playerArray[i];
            playerArray[i] = tmp;
        }
        return playerArray;
    }
}

