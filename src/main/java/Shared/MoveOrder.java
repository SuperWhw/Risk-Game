package Shared;

import java.util.HashMap;

public class MoveOrder extends OrderBasic {

    public MoveOrder(Player p, String orderType, Territory fromT, Territory toT, int level, int units) {
        super(p, orderType, fromT, toT, level, units);
    }

    @Override
    public void execute() {
        if(fromT != null) fromT.removeUnits(level, units);
        if(toT != null) toT.addUnits(level, units);
    }
}
