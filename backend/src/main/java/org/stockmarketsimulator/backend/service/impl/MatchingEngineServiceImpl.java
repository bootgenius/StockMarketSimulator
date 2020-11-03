package org.stockmarketsimulator.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stockmarketsimulator.backend.domain.*;
import org.stockmarketsimulator.backend.service.GatewayCallback;
import org.stockmarketsimulator.backend.service.MatchingEngineService;
import org.stockmarketsimulator.backend.service.OrderBooksService;
import org.stockmarketsimulator.backend.service.TradeLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class MatchingEngineServiceImpl implements MatchingEngineService {

    //region Fields
    private final Map<String, OrderBooksService> bookServices = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(MatchingEngineServiceImpl.class);

    @Autowired
    private GatewayCallback gatewayCallback;

    @Autowired
    private TradeLedgerService tradeLedgerService;

    private final ScheduledExecutorService balanceBookScheduler = Executors.newSingleThreadScheduledExecutor();

    private final ScheduledExecutorService priceHistoryScheduler = Executors.newSingleThreadScheduledExecutor();
    //endregion

    //region Public Methods
    @Override
    public void addOrder(Order order) {
        OrderBooksService orderBooksService = bookServices.computeIfAbsent(order.getSymbol(), key -> new OrderBooksServiceImpl());
        synchronized (orderBooksService) {
            orderBooksService.addOrder(order);
            gatewayCallback.sendLogMessage(String.format("Order with ID %s added: %s %s %s @ %s",
                    order.getId(),
                    order.getSymbol(),
                    order.getType(),
                    order.getQuantity(),
                    order.getPrice()));

            gatewayCallback.sendOrdersChanged(order.getClientId(), order.getSymbol());
            gatewayCallback.sendOrderBookChanged(order.getSymbol());
        }
    }

    @Override
    public void cancelOrder(String clientId, String symbol, Integer orderId) {
        OrderBooksService orderBooksService = bookServices.computeIfAbsent(symbol, key -> new OrderBooksServiceImpl());
        synchronized (orderBooksService) {
            Order cancelledOrder = orderBooksService.removeOrder(orderId);
            if (cancelledOrder != null) {
                gatewayCallback.sendLogMessage(String.format("Order with ID %s cancelled: %s %s %s @ %s",
                        cancelledOrder.getId(),
                        cancelledOrder.getSymbol(),
                        cancelledOrder.getType(),
                        cancelledOrder.getQuantity(),
                        cancelledOrder.getPrice()));
            }
            else {
                gatewayCallback.sendLogMessage(String.format("Can not cancel the order. Order for symbol %s and ID %s is not found",
                        symbol,
                        orderId));
            }
        }

        gatewayCallback.sendOrdersChanged(clientId, symbol);
        gatewayCallback.sendOrderBookChanged(symbol);
    }

    public List<PriceHistoryItem> getPriceHistory(String symbol, PriceHistoryType priceHistoryType) {
        OrderBooksService orderBooksService = bookServices.computeIfAbsent(symbol, key -> new OrderBooksServiceImpl());

        List<PriceHistoryItem> resultList;
        switch (priceHistoryType) {
            case SECOND5:
                resultList = getPriceHistoryFor5SecondsTimeFrame(orderBooksService);
                break;
            default:
                throw new RuntimeException(String.format("Price history type is not supported yet", priceHistoryType));
        }

        return resultList.stream()
                .sorted(Comparator.comparing(PriceHistoryItem::getTimeStamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrders(String clientId, String symbol) {
        OrderBooksService orderBooksService = bookServices.computeIfAbsent(symbol, key -> new OrderBooksServiceImpl());
        return orderBooksService.getOrders(clientId);
    }

    @Override
    public List<OrderBookItem> getOrderBook(String symbol) {
        OrderBooksService orderBooksService = bookServices.computeIfAbsent(symbol, key -> new OrderBooksServiceImpl());

        ArrayList<Order> orders = new ArrayList<>(orderBooksService.getOrders());

        return orders.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(o -> new AbstractMap.SimpleEntry<>(o.getType(), o.getPrice())))
                .entrySet()
                .stream()
                .map(e -> new OrderBookItem(e.getKey().getKey(), e.getKey().getValue(), e.getValue().stream().mapToInt(Order::getQuantity).sum()))
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void postConstruct() {
        balanceBookScheduler.scheduleAtFixedRate(this::balanceBooks, 1, 1, TimeUnit.SECONDS);
        priceHistoryScheduler.scheduleAtFixedRate(this::fixPriceHistory, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void preDestroy() {
        balanceBookScheduler.shutdown();
        priceHistoryScheduler.shutdown();
    }
    //endregion

    //region Private Methods
    private void fixPriceHistory() {
        try {
            for (String symbol : bookServices.keySet()) {
                OrderBooksService orderBooksService = bookServices.get(symbol);

                Order orderWithMaxPrice = orderBooksService.getOrders().stream()
                        .filter(o -> OrderType.BUY.equals(o.getType()))
                        .max(Comparator.comparingInt(Order::getPrice)).orElse(null);
                if (orderWithMaxPrice != null) {
                    orderBooksService.addPriceHistory(LocalDateTime.now(), orderWithMaxPrice.getPrice());
                }
                gatewayCallback.sendPriceHistoryChanged(symbol);
            }
        } catch (Exception ex) {
            logger.error("An error occurred while fixing price history", ex);
        }
    }

    private void balanceBooks() {
        try {
            Map<String, Set<String>> clientsWithOrdersChanged = new HashMap<>();
            Set<String> orderBooksChanged = new HashSet<>();

            for (String symbol : bookServices.keySet()) {

                OrderBooksService orderBooksService = bookServices.get(symbol);

                synchronized (orderBooksService) {
                    List<Order> orders = new ArrayList<>(orderBooksService.getOrders());

                    AbstractMap.SimpleEntry<Order, Order> matchingOrders;
                    do {
                        matchingOrders = getMatchingOrders(orders);
                        if (matchingOrders != null) {
                            Order buyOrder = matchingOrders.getKey();
                            Order sellOrder = matchingOrders.getValue();

                            Trade trade = new Trade();
                            trade.setBuyOrderId(buyOrder.getId());
                            trade.setSellOrderId(sellOrder.getId());
                            trade.setSymbol(symbol);
                            if (buyOrder.getQuantity() >= sellOrder.getQuantity()) {
                                trade.setPrice(sellOrder.getPrice());
                                trade.setQuantity(sellOrder.getQuantity());

                                int newBuyOrderQuantity = buyOrder.getQuantity() - sellOrder.getQuantity();
                                buyOrder.setQuantity(newBuyOrderQuantity);
                                sellOrder.setQuantity(0);
                            } else {
                                trade.setPrice(buyOrder.getPrice());
                                trade.setQuantity(buyOrder.getQuantity());

                                int newSellOrderQuantity = sellOrder.getQuantity() - buyOrder.getQuantity();
                                sellOrder.setQuantity(newSellOrderQuantity);
                                buyOrder.setQuantity(0);
                            }

                            //Log trade and send message to clients
                            tradeLedgerService.logTrade(trade);
                            gatewayCallback.sendLogMessage(String.format("New execution with ID %s: %s %s @ %s (orders %s and %s)",
                                    trade.getId(),
                                    trade.getSymbol(),
                                    trade.getQuantity(),
                                    trade.getPrice(),
                                    trade.getBuyOrderId(),
                                    trade.getSellOrderId()));
                            clientsWithOrdersChanged.computeIfAbsent(symbol, s -> new HashSet<>()).add(buyOrder.getClientId());
                            clientsWithOrdersChanged.computeIfAbsent(symbol, s -> new HashSet<>()).add(sellOrder.getClientId());
                            orderBooksChanged.add(symbol);

                            if (buyOrder.getQuantity() == 0) {
                                orders.remove(buyOrder);
                                orderBooksService.removeOrder(buyOrder.getId());
                            }

                            if (sellOrder.getQuantity() == 0) {
                                orders.remove(sellOrder);
                                orderBooksService.removeOrder(sellOrder.getId());
                            }
                        }
                    }
                    while (matchingOrders != null);
                }
            }

            for (String symbol : clientsWithOrdersChanged.keySet()) {
                for (String clientId : clientsWithOrdersChanged.get(symbol)) {
                    gatewayCallback.sendOrdersChanged(clientId, symbol);
                }
            }

            for (String symbol : orderBooksChanged) {
                gatewayCallback.sendOrderBookChanged(symbol);
            }
        } catch (Exception ex) {
            logger.error("An error occurred while balancing the books", ex);
        }
    }

    private AbstractMap.SimpleEntry<Order, Order> getMatchingOrders(Collection<Order> orders) {
        List<Order> buyOrdersSorted = orders.stream()
                .filter(o -> OrderType.BUY.equals(o.getType()))
                .sorted((o1, o2) -> {
                    int compareByPrice = Integer.compare(o2.getPrice(), o1.getPrice());
                    if (compareByPrice != 0) {
                        return compareByPrice;
                    }

                    return o1.getCreated().compareTo(o2.getCreated());
                })
                .collect(Collectors.toList());

        List<Order> sellOrdersSorted = orders.stream()
                .filter(o -> OrderType.SELL.equals(o.getType()))
                .sorted(Comparator.comparingInt(Order::getPrice).thenComparing(Order::getCreated))
                .collect(Collectors.toList());

        for (Order buyOrder : buyOrdersSorted) {
            List<Order> matchingSellOrders = sellOrdersSorted.stream()
                    .filter(so -> buyOrder.getPrice() >= so.getPrice())
                    .collect(Collectors.toList());
            if (matchingSellOrders.size() > 0) {
                return new AbstractMap.SimpleEntry<>(buyOrder, sellOrdersSorted.get(0));
            }
        }
        return null;
    }

    private List<PriceHistoryItem> getPriceHistoryFor5SecondsTimeFrame(OrderBooksService orderBooksService) {
        List<PriceHistoryItem> resultList = new ArrayList<>();

        Map<Integer, List<Map.Entry<LocalDateTime, Integer>>> groupByYear = orderBooksService.getPriceHistory().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(groupingBy(x -> x.getKey().getYear()));

        for (Integer year : groupByYear.keySet()) {
            Map<Month, List<Map.Entry<LocalDateTime, Integer>>> groupByMonth = groupByYear.get(year).stream()
                    .collect(groupingBy(x -> x.getKey().getMonth()));

            for (Month month : groupByMonth.keySet()) {
                Map<Integer, List<Map.Entry<LocalDateTime, Integer>>> groupByDay = groupByMonth.get(month).stream()
                        .collect(groupingBy(x -> x.getKey().getDayOfMonth()));

                for (Integer day : groupByDay.keySet()) {
                    Map<Integer, List<Map.Entry<LocalDateTime, Integer>>> groupBySeconds = groupByDay.get(day).stream()
                            .collect(groupingBy(x -> x.getKey().toLocalTime().get(ChronoField.SECOND_OF_DAY) / 5));

                    for (Integer second : groupBySeconds.keySet()) {
                        List<Map.Entry<LocalDateTime, Integer>> entries = groupBySeconds.get(second);

                        Map.Entry<LocalDateTime, Integer> max = entries.stream().max(Comparator.comparingInt(Map.Entry::getValue)).orElse(null);
                        Map.Entry<LocalDateTime, Integer> min = entries.stream().min(Comparator.comparingInt(Map.Entry::getValue)).orElse(null);

                        PriceHistoryItem priceHistoryItem = new PriceHistoryItem();
                        priceHistoryItem.setHigh(max.getValue());
                        priceHistoryItem.setLow(min.getValue());
                        priceHistoryItem.setOpen(entries.get(0).getValue());
                        priceHistoryItem.setClose(entries.get(entries.size() - 1).getValue());
                        //Volume is not supported yet
                        //priceHistoryItem.setVolume(196);
                        priceHistoryItem.setTimeStamp(entries.get(0).getKey());

                        resultList.add(priceHistoryItem);
                    }
                }
            }
        }

        return resultList;
    }

    //endregion
}
