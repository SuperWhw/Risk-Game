package Client;

class MoveOrder extends Order{
    private Territory fromT;
    private Territory toT;
    private int units;

    public MoveOrder(Player p, String orderType, Territory fromT, Territory toT, int units) {
        super(p, orderType);
        this.fromT = fromT;
        this.toT = toT;
        this.units = units;
    }

    @Override
    public void execute() {
        toT.addUnits(units);
        fromT.removeUnits(units);
    }
}
