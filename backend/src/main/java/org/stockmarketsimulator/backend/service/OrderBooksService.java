package org.stockmarketsimulator.backend.service;

import org.stockmarketsimulator.backend.domain.Order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderBooksService {

    void addPriceHistory(LocalDateTime date, Integer price);

    Map<LocalDateTime, Integer> getPriceHistory();

    void addOrder(Order order);

    Order removeOrder(Integer orderId);

    List<Order> getOrders();

    List<Order> getOrders(String clientId);
}
