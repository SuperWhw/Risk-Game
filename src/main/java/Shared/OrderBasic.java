package Shared;

public abstract class OrderBasic extends Order {
    protected Player player;
    protected String orderType;
    protected Territory fromT;
    protected Territory toT;
    protected int units;

    public OrderBasic(Player p, String orderType, Territory fromT, Territory toT, int units) {
        super(p, orderType);
        this.fromT = fromT;
        this.toT = toT;
        this.units = units;
    }

    public Territory getFromT() {
        return fromT;
    }

    public Territory getToT() {
        return toT;
    }

    public int getUnits() {
        return units;
    }

    public abstract void execute();
}
