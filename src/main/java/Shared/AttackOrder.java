package Shared;

import java.util.HashMap;
import java.util.Random;

public class AttackOrder extends OrderBasic {
    HashMap<Integer, Integer> att_units;

    public AttackOrder(Player p, String orderType, Territory fromT, Territory toT, int level, int units) {
        super(p, orderType, fromT, toT, level, units);
    }

    private int rollDice(int from, int to) {
        Random rand = new Random();
        return rand.nextInt((to - from) + 1) + from;
    }

    public void setAttUnits(HashMap<Integer, Integer> att_units) {
        this.att_units = att_units;
    }

    private int getTotalUnits(HashMap<Integer, Integer> unitsMap) {
        int res = 0;
        for(int level = 0; level <= 6; ++level) {
            res += unitsMap.get(level);
        }
        return res;
    }

    @Override
    public void execute() {
        HashMap<Integer, Integer> def_units = toT.getUnitsMap();
        Player attacker = this.getPlayer();
        UpgradeHelper uh = new UpgradeHelper();

        int att_low_level = 0, att_high_level = 6, def_low_level = 0, def_high_level = 6;
        int att_tot = getTotalUnits(att_units), def_tot = getTotalUnits(def_units), attNum, defNum;

        while(att_tot > 0 && def_tot > 0) {
            // highest level attacker VS lowest level defender
            while(att_units.get(att_high_level) == 0) {
                att_high_level--;
            }
            while(def_units.get(def_low_level) == 0) {
                def_low_level++;
            }
            attNum = rollDice(1, 20);
            defNum = rollDice(1, 20);
            if (attNum + uh.levelBonus[att_high_level] > defNum + uh.levelBonus[def_low_level]) {
                int units = def_units.get(def_low_level);
                def_units.put(def_low_level, --units);
                def_tot--;
            }
            else {
                int units = att_units.get(att_high_level);
                att_units.put(att_high_level, --units);
                att_tot--;
            }
            if(att_tot == 0 || def_tot == 0) break;

            // highest level defender VS lowest level attacker
            while(att_units.get(att_low_level) == 0) {
                att_low_level++;
            }
            while(def_units.get(def_high_level) == 0) {
                def_high_level--;
            }
            attNum = rollDice(1, 20);
            defNum = rollDice(1, 20);
            if (attNum + uh.levelBonus[att_low_level] > defNum + uh.levelBonus[def_high_level]) {
                int units = def_units.get(def_high_level);
                def_units.put(def_high_level, --units);
                def_tot--;
            }
            else {
                int units = att_units.get(att_low_level);
                att_units.put(att_low_level, --units);
                att_tot--;
            }
        }

        if(att_tot > 0) {  // attacker wins
            toT.setUnitsMap(att_units);
            toT.setOwner(attacker);
        }
        else {  //defender wins
            toT.setUnitsMap(def_units);
        }
    }
}
