package org.stockmarketsimulator.backend.domain;

public class OrderBookItem {

    //region Fields
    private OrderType type;

    private Integer price;

    private Integer quantity;
    //endregion

    //region Properties
    public Integer getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderType getType() {
        return type;
    }
    //endregion

    //region .ctor
    public OrderBookItem(OrderType type, Integer price, Integer quantity) {
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }
    //endregion
}
