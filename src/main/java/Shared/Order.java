package Shared;

public abstract class Order {
    protected Player player;
    protected String orderType;

    public Order(Player p, String orderType) {
        this.player = p;
        this.orderType = orderType;
    }

    public Player getPlayer() {
        return player;
    }

    public String getOrderType() {
        return orderType;
    }

    public abstract void execute();

}
