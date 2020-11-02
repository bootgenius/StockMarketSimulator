package org.stockmarketsimulator.backend.service;

public interface GatewayCallback {

    void sendLogMessage(String message);

    void sendOrderBookChanged(String symbol);

    void sendOrdersChanged(String clientId, String symbol);

    void sendPriceHistoryChanged(String symbol);
}
