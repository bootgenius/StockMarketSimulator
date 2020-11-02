package org.stockmarketsimulator.backend.service.impl;

import org.stockmarketsimulator.backend.domain.Trade;
import org.stockmarketsimulator.backend.helper.IdGenerator;
import org.stockmarketsimulator.backend.service.TradeLedgerService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TradeLedgerServiceImpl implements TradeLedgerService {

    //region Fields
    private final IdGenerator tradeIdGenerator = new IdGenerator();

    private final Map<Integer, Trade> trades = new ConcurrentHashMap<>();
    //endregion

    //region Public Methods
    @Override
    public void logTrade(Trade trade) {
        trade.setId(tradeIdGenerator.generateNewId());
        trade.setCreated(new Date());
        trades.put(trade.getId(), trade);
    }
    //endregion
}
