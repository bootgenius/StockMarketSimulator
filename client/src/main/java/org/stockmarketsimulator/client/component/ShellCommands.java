package org.stockmarketsimulator.client.component;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ShellComponent
public class ShellCommands {

    //region Enums
    public enum OrderType {
        B,
        S
    }
    //endregion

    //region Fields

    private final String OK_RESPONSE = "OK";

    private final String ERROR_RESPONSE = "ERROR";

    @Value("${app.demo1Symbol}")
    private String demo1Symbol;

    @Value("${app.demo1Count}")
    private Integer demo1Count;

    @Value("${app.serverRestAddress}")
    private String serverRestApiAddress;

    @Value("${app.clientId}")
    private String clientId;

    private final Logger logger = LoggerFactory.getLogger(ShellCommands.class);
    //endregion

    //region Public Methods
    @ShellMethod(value = "Greeting by name", key = "greet")
    public String greet(String name) {
        return "Hello " + name;
    }

    @ShellMethod(value = "Delete order by id", key = "del")
    public String del(String symbol,
                      Integer orderId) {
        try {
            boolean postResult = sendPostRequest("/cancelOrder",
                    Arrays.asList(new BasicNameValuePair("symbol", symbol),
                            new BasicNameValuePair("clientId", clientId),
                            new BasicNameValuePair("orderId", orderId.toString())));

            return postResult ? OK_RESPONSE : ERROR_RESPONSE;
        } catch (Exception ex) {
            logger.error("An error occurred while deleting the order", ex);
            return ERROR_RESPONSE;
        }
    }

    @ShellMethod(value = "Add new order", key = "add")
    public String add(String symbol,
                      OrderType orderType,
                      Integer quantity,
                      Integer price) {
        try {
            boolean postResult = sendPostRequest("/addOrder",
                    createAddParameters(symbol,orderType, quantity, price));

            return postResult ? OK_RESPONSE : ERROR_RESPONSE;
        } catch (Exception ex) {
            logger.error("An error occurred while adding the order", ex);
            return ERROR_RESPONSE;
        }
    }

    @ShellMethod(value = "Demo 1", key = "demo1")
    public String demo1() {
        try {
            Random random = new Random();

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

            for(int i = 0; i < demo1Count; i++) {

                OrderType orderType = random.nextBoolean() ? OrderType.B: OrderType.S;
                Integer price = random.nextInt(20) + 50;
                Integer quantity = random.nextInt(100) + 100;

                ScheduledFuture<?> schedule = executorService.schedule(() -> {
                    sendPostRequest("/addOrder",
                            createAddParameters(demo1Symbol, orderType, quantity, price));
                }, 200, TimeUnit.MILLISECONDS);
                schedule.get();
            }

            return OK_RESPONSE;
        } catch (Exception ex) {
            logger.error("An error occurred while executing demo1 method", ex);
            return ERROR_RESPONSE;
        }
    }

    //endregion

    //region Private Methods

    private List<NameValuePair> createAddParameters(String symbol,
                                                    OrderType orderType,
                                                    Integer quantity,
                                                    Integer price){
        return Arrays.asList(new BasicNameValuePair("symbol", symbol),
                new BasicNameValuePair("clientId", clientId),
                new BasicNameValuePair("type", orderType == OrderType.B ? "BUY" : "SELL"),
                new BasicNameValuePair("quantity", quantity.toString()),
                new BasicNameValuePair("price", price.toString()));
    }

    private boolean sendPostRequest(String path,
                                    List<NameValuePair> params) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URI uri = new URIBuilder(serverRestApiAddress + path).build();
            HttpPost httpPost = new HttpPost(uri);

            httpPost.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                return true;
            }
            else {
                logger.error(response.getStatusLine().toString());
                return false;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    //endregion
}
