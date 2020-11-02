package org.stockmarketsimulator.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.stockmarketsimulator.backend.domain.Order;
import org.stockmarketsimulator.backend.domain.OrderBookItem;
import org.stockmarketsimulator.backend.domain.PriceHistoryItem;
import org.stockmarketsimulator.backend.domain.PriceHistoryType;
import org.stockmarketsimulator.backend.service.GatewayCallback;
import org.stockmarketsimulator.backend.service.GatewayService;
import org.stockmarketsimulator.backend.service.MatchingEngineService;

import java.util.List;

@Service
public class GatewayServiceImpl implements GatewayService, GatewayCallback {

    //region Fields
    @Autowired
    private MatchingEngineService matchingEngineService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    //endregion

    //region Public Methods
    @Override
    public void addOrder(Order order) {
        matchingEngineService.addOrder(order);
    }

    @Override
    public void cancelOrder(String clientId, String symbol, Integer orderId) {
        matchingEngineService.cancelOrder(clientId, symbol, orderId);
    }

    @Override
    public List<Order> getOrders(String clientId, String symbol) {
        return matchingEngineService.getOrders(clientId, symbol);
    }

    @Override
    public List<PriceHistoryItem> getPriceHistory(String symbol, PriceHistoryType priceHistoryType) {
        return matchingEngineService.getPriceHistory(symbol, priceHistoryType);
    }

    @Override
    public List<OrderBookItem> getOrderBook(String symbol) {
        return matchingEngineService.getOrderBook(symbol);
    }

    @Override
    public void sendLogMessage(String message) {
        simpMessagingTemplate.convertAndSend("/topic/logs", message);
        System.out.println(message);
    }

    @Override
    public void sendOrderBookChanged(String symbol) {
        simpMessagingTemplate.convertAndSend("/topic/orderBookChanged", symbol);
    }

    @Override
    public void sendPriceHistoryChanged(String symbol) {
        simpMessagingTemplate.convertAndSend("/topic/priceHistoryChanged", symbol);
    }

    @Override
    public void sendOrdersChanged(String clientId, String symbol) {
        simpMessagingTemplate.convertAndSendToUser(clientId, "/queue/ordersChanged", symbol);
    }

    //endregion
}
