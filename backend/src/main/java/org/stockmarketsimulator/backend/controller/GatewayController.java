package org.stockmarketsimulator.backend.controller;

import org.stockmarketsimulator.backend.domain.*;
import org.stockmarketsimulator.backend.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("gateway")
public class GatewayController {

    //region Constants
    private final static List<Symbol> symbols = new ArrayList<Symbol>() {
        {
            add(new Symbol("AAPL", "Apple Inc."));
            add(new Symbol("MSFT", "Microsoft Corp."));
            add(new Symbol("AMZN", "Amazon.com Inc."));
            add(new Symbol("FB", "Facebook Inc."));
            add(new Symbol("GOOGL", "Alphabet Inc. Class A Shares"));
            add(new Symbol("GOOG", "Alphabet Inc. Class C Shares"));
            add(new Symbol("BRK.B", "Berkshire Hathaway Inc."));
            add(new Symbol("JNJ", "Johnson & Johnson"));
            add(new Symbol("P", "Procter & Gamble"));
        }
    };
    //endregion

    //region Fields
    @Autowired
    private GatewayService gatewayService;

    //endregion

    //region Public Methods

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
    public String healthCheck() {
        return "OK";
    }

    @RequestMapping(value = "/symbols", method = RequestMethod.GET)
    public List<Symbol> getSymbols() {
        return symbols;
    }

    @RequestMapping(value = "/priceHistory", method = RequestMethod.GET)
    public List<PriceHistoryItem> getPriceHistory(@RequestParam String symbol,
                                       @RequestParam PriceHistoryType priceHistoryType) {
        return gatewayService.getPriceHistory(symbol, priceHistoryType);
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public List<Order> getOrders(@RequestParam String symbol,
                                 @RequestParam String clientId) {
        return gatewayService.getOrders(clientId, symbol).stream()
                .sorted(Comparator.comparing(Order::getCreated))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/orderBook", method = RequestMethod.GET)
    public List<OrderBookItem> getOrderBook(@RequestParam String symbol) {
        return gatewayService.getOrderBook(symbol);
    }


    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    public void addOrder(@RequestParam String clientId,
                         @RequestParam String symbol,
                         @RequestParam OrderType type,
                         @RequestParam Integer quantity,
                         @RequestParam Integer price) {
        Order order = new Order();
        order.setSymbol(symbol);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setType(type);
        order.setClientId(clientId);

        gatewayService.addOrder(order);
    }

    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public void cancelOrder(@RequestParam String clientId,
                            @RequestParam String symbol,
                            @RequestParam Integer orderId) {
        gatewayService.cancelOrder(clientId, symbol, orderId);
    }
    //endregion
}
