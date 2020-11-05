package org.stockmarketsimulator.backend.service.impl;

import org.stockmarketsimulator.backend.domain.Order;
import org.stockmarketsimulator.backend.helper.IdGenerator;
import org.stockmarketsimulator.backend.service.OrderBooksService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderBooksServiceImpl implements OrderBooksService {

    //region Fields
    private Map<LocalDateTime, Integer> priceHistory = new ConcurrentHashMap<>();

    private final List<Order> orders = Collections.synchronizedList(new ArrayList<>());

    private final IdGenerator idGenerator = new IdGenerator();

    //endregion

    //region Public Methods
    @Override
    public void addOrder(Order order) {
        order.setId(idGenerator.generateNewId());
        order.setCreated(new Date());
        orders.add(order);
    }

    @Override
    public Order removeOrder(Integer orderId) {
        Order existedOrder = orders.stream().filter(o -> orderId.equals(o.getId())).findAny().orElse(null);
        if (existedOrder != null) {
            orders.remove(existedOrder);
            return existedOrder;
        } else {
            return null;
        }
    }

    @Override
    public List<Order> getOrders() {
        return Collections.unmodifiableList(this.orders);
    }

    @Override
    public List<Order> getOrders(String clientId) {
        return this.orders.stream()
                .filter(o -> clientId.equals(o.getClientId()))
                .collect(Collectors.toList());
    }

    @Override
    public void addPriceHistory(LocalDateTime date, Integer price) {
        priceHistory.put(date, price);
    }

    @Override
    public Map<LocalDateTime, Integer> getPriceHistory() {
        return priceHistory;
    }

    //endregion
}
