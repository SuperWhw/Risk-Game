package Server;

import Shared.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class OrderHandler {

    public void refreshRoundEnd(GameMap map) {
        for(var territory : map.getTerritoryMap().values()) {
            territory.setUnits(0, territory.getUnits(0) + 1);
        }
        for(var player: map.getPlayerMap().values()) {
            player.refreshFoodTotal();
            player.refreshTechTotal();
        }
    }

    public void execute(ArrayList<Order> orderList) {

        ArrayList<AttackOrder> attackList = new ArrayList<>();
        CheckHelper checker = new CheckHelper();
        ArrayList<Player> upgradeTechPlayerList = new ArrayList<>();

        // execute move, upgrade units and first step of attack
        for(var order: orderList) {
            if(!checker.checkOrder(order)) continue;

            switch (order.getOrderType()) {
                case "move": {
                    MoveOrder moveOrder = (MoveOrder) order;
                    moveOrder.execute();
                    break;
                }
                case "upgrade":
                    UpgradeOrder upgradeOrder = (UpgradeOrder) order;
                    if (upgradeOrder.getTerritory() != null) {
                        upgradeOrder.execute();
                    } else {
                        upgradeTechPlayerList.add(order.getPlayer());
                    }
                    break;
                case "attack": {
                    // Attackers should leave out of their owner Territory first.
                    AttackOrder attackOrder = (AttackOrder) order;
                    MoveOrder moveOrder = new MoveOrder(order.getPlayer(), "move", attackOrder.getFromT(), null, attackOrder.getLevel(), attackOrder.getUnits());
                    moveOrder.execute();
                    attackList.add(attackOrder);
                    break;
                }
            }
        }

        // execute attack
        executeMergeAttack(attackList);

        // execute tech level upgrade
        for(var p: upgradeTechPlayerList) {
            UpgradeOrder upgradeOrder = new UpgradeOrder(p,"upgrade");
            upgradeOrder.execute();
        }
    }

    private void executeMergeAttack(ArrayList<AttackOrder> attackList) {

        // merge force if attackers attack same territory who are from same player
        HashMap<Territory, HashMap<Player, HashMap<Integer, Integer>>> attHandler = mergeAttackForce(attackList);

        for(var t: attHandler.keySet()) {
            // get random player list
            Player[] playerArray = getRandomPlayerArray(attHandler.get(t).keySet().toArray(new Player[0]));

            for(var p: playerArray) {
                // we don't need fromT, level, units; so the parameters are dummy
                AttackOrder attackOrder = new AttackOrder(p, "attack", null, t, -1, -1);
                attackOrder.setAttUnits(attHandler.get(t).get(p));
                attackOrder.execute();
            }
        }
    }

    private HashMap<Territory, HashMap<Player, HashMap<Integer, Integer>>> mergeAttackForce(ArrayList<AttackOrder> attackList) {
        HashMap<Territory, HashMap<Player, HashMap<Integer, Integer>>> attHandler = new HashMap<>();
        for(var att: attackList) {
            Territory toT = att.getToT();
            if(!attHandler.containsKey(toT)) {
                attHandler.put(toT, new HashMap<>());
            }
            Player p = att.getPlayer();
            if(!attHandler.get(toT).containsKey(p)) {
                attHandler.get(toT).put(att.getPlayer(), new HashMap<>(){{
                    for(int level = 0; level <= 6; ++level) {
                        put(level,0);
                    }
                }});
            }
            int level = att.getLevel(), units = att.getUnits(), originalUnits = attHandler.get(toT).get(p).get(level);
            attHandler.get(toT).get(p).put(level, originalUnits + units);
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

