package org.stockmarketsimulator.client.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketReceiver extends StompSessionHandlerAdapter {

    //region Fields
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final WebSocketStompClient webSocketStompClient;

    private final Logger logger = LoggerFactory.getLogger(WebSocketReceiver.class);

    @Value("${app.serverWebSocketAddress}")
    private String serverWebSocketAddress;

    //endregion

    //region .ctor

    public WebSocketReceiver() {
        WebSocketClient client = new StandardWebSocketClient();
        webSocketStompClient = new WebSocketStompClient(client);
        webSocketStompClient.setMessageConverter(new StringMessageConverter());
    }

    //endregion

    //region Public Methods
    @PostConstruct
    public void postConstruct() {
        run();
    }

    @PreDestroy
    public void preDestroy() {
        executorService.shutdown();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.info("WebSocket error");
        super.handleTransportError(session, exception);
        executorService.schedule(this::run,1, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("WebSocket Connected");
        session.subscribe("/topic/logs", this);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info(payload.toString());
    }

    //endregion

    //region Private Methods
    private void run() {
        webSocketStompClient.connect(serverWebSocketAddress, this);
    }
    //endregion
}
