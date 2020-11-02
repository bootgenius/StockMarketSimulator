package org.stockmarketsimulator.backend.domain;

public class WebSocketMessage {

    //region Fields
    private String from;

    private String message;
    //endregion

    //region Properties
    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }
    //endregion
}
