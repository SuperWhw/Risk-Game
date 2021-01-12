package Shared;

import java.util.HashMap;

public abstract class OrderBasic extends Order {
    protected Player player;
    protected Territory fromT;
    protected Territory toT;
    protected int level;
    protected int units;

    public OrderBasic(Player p, String orderType, Territory fromT, Territory toT, int level, int units) {
        super(p, orderType);
        this.fromT = fromT;
        this.toT = toT;
        this.units = units;
        this.level = level;
    }

    public Territory getFromT() {
        return fromT;
    }

    public Territory getToT() {
        return toT;
    }


    public int getLevel() {
        return level;
    }

    public int getUnits() {
        return units;
    }

    public abstract void execute();
}
