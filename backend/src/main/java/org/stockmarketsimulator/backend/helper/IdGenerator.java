package org.stockmarketsimulator.backend.helper;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    //region Fields
    private AtomicInteger id = new AtomicInteger(0);
    //endregion

    //region Public Methods
    public Integer generateNewId() {
        return id.incrementAndGet();
    }
    //endregion

}
