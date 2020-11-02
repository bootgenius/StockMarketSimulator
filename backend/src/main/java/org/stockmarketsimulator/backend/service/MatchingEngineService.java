package org.stockmarketsimulator.backend.service;

import org.stockmarketsimulator.backend.domain.Order;
import org.stockmarketsimulator.backend.domain.OrderBookItem;
import org.stockmarketsimulator.backend.domain.PriceHistoryItem;
import org.stockmarketsimulator.backend.domain.PriceHistoryType;

import java.util.List;

public interface MatchingEngineService {

    public void addOrder(Order order);

    public void cancelOrder(String clientId, String symbol, Integer orderId);

    public List<Order> getOrders(String clientId, String symbol);

    List<OrderBookItem> getOrderBook(String symbol);

    List<PriceHistoryItem> getPriceHistory(String symbol, PriceHistoryType priceHistoryType);
}
