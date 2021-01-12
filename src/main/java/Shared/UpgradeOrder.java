package Shared;

public class UpgradeOrder extends Order{
    private Territory t;
    private int fromLevel;
    private int toLevel;
    private int units;

    public UpgradeOrder(Player p, String orderType) {
        super(p, orderType);
    }

    public UpgradeOrder(Player p, String orderType, Territory t, int fromLevel, int toLevel, int units) {
        super(p, orderType);
        this.t = t;
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.units = units;
    }

    public Territory getTerritory() {
        return t;
    }

    public int getFromLevel() {
        return fromLevel;
    }

    public int getToLevel() {
        return toLevel;
    }

    public int getUnits() {
        return units;
    }

    @Override
    public void execute() {
        // Upgrade tech level
        if(t == null) {
            try {
                player.setTechLevel(player.getTechLevel() + 1);
            } catch (IllegalArgumentException e) {
                System.out.println("Level should in range 1-6!");
            }
        }
        else { // upgrade units
            try {
                t.removeUnits(fromLevel,units);
                t.addUnits(toLevel,units);
            }
            catch (IllegalArgumentException e) {
                System.out.println("Illegal units upgrade!");
            }
        }

    }
}
