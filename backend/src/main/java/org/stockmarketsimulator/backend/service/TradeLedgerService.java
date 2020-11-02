package org.stockmarketsimulator.backend.service;

import org.stockmarketsimulator.backend.domain.Trade;

public interface TradeLedgerService {

    public void logTrade(Trade trade);

}
