package Client;

class MoveOrder extends OrderBasic {

    public MoveOrder(Player p, String orderType, Territory fromT, Territory toT, int units) {
        super(p, orderType, fromT, toT, units);
    }

    @Override
    public void execute() {
        toT.addUnits(units);
        fromT.removeUnits(units);
    }
}
