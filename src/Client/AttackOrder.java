package Client;

import java.util.Random;

class AttackOrder extends Order{
    private Territory fromT;
    private Territory toT;
    private int units;

    public AttackOrder(Player p, String orderType, Territory fromT, Territory toT, int units) {
        super(p, orderType);
        this.fromT = fromT;
        this.toT = toT;
        this.units = units;
    }

    private int getRandom(int from, int to) {
        Random rand = new Random();
        return rand.nextInt((to - from) + 1) + from;
    }

    @Override
    public void execute() {
        // Attackers should leave out of their owner Territory first.
        fromT.removeUnits(units);

        int att_units = units, def_units = toT.getUnits();
        while(att_units > 0 && def_units > 0) {
//            int attNum = getRandom(1, 20);
//            int defNum = getRandom(1, 20);
//            if (attNum > defNum) def_units--;
//            else att_units--;

            // for accurate testing, we don't use random number now
            if (att_units > def_units) {
                att_units -= def_units;
                def_units = 0;
            }
            if (def_units > att_units) {
                def_units -= att_units;
                att_units = 0;
            }
            if (def_units == att_units) {
                def_units = 1;
                att_units = 0;
            }
        }
        if(att_units > 0) {
            toT.setUnits(att_units);
            toT.setOwner(fromT.getOwner());
        }
        else {
            toT.setUnits(def_units);
        }
    }
}
