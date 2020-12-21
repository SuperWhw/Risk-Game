package Shared;

public class MoveOrder extends OrderBasic {

    public MoveOrder(Player p, String orderType, Territory fromT, Territory toT, int units) {
        super(p, orderType, fromT, toT, units);
    }

    @Override
    public void execute() {
        if(fromT != null) fromT.removeUnits(units);
        if(toT != null) toT.addUnits(units);
    }
}
