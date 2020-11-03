# StockMarketSimulator

This project emulates stock market<br>
Clients of the stock market will interact with it using a REST and WebSocket API.<br>
Task description for this project can be found [HERE](https://github.com/BootGenius/StockMarketSimulator/blob/main/docs/TaskDescription.pdf) <br>
Demo video can be found [HERE](https://youtu.be/Ykd8YTh0Rvc) <br>
<br>
The solution consists of 3 projects:
1. Backend - the backend application based on Spring Boot application
2. Client - the console client based on Spring Boot and Spring Shell
3. Webclient - the web client based on Vuejs and Vuetify framework

## Backend
The backend project is a Spring Boot application that contains main logic for creating/deleting orders, balancing the books, and executing trades.<br>
Application has a websocket support to send logs about server events to client applications

* Application has a REST API to consume requests from the clients
* REST API endpoint: http://localhost:8080/gateway
* Application has WebSocket support to send server events to client applications
* WebSocket endpoint: ws://localhost:8080/ws
* By default, the application is running on the 8080 port<br>

**Prerequisites:**
- JDK 1.8 installed<br>

**How to build and run:**
```
gradlew.bat build
cd ./backend/build/libs/
java -jar ./backend.jar
```

## Client
This project implements console client to create/delete orders and batch order creation for demonstration purposes.<br>
The application sends requests to the server through REST API and gets responses through a WebSocket connection.<br>
By default clientId for the console application is "client2"<br>


When you run this application you can use the following commands:<br>
* **help** - to get a list of commands available
* **add** - to add a new order (e.g. add AAPL B 100 50)
* **del** - to delete an order by id (e.g. del AAPL 5)
* **demo1** - to run the batch orders creation. By default after this command, 1000 orders will be created with the interval of 200 milliseconds

**Prerequisites:**
- JDK 1.8 installed<br>

**How to build and run:**
```
gradlew.bat build
cd ./client/build/libs/
java -jar ./client.jar
```

## Webclient
This project implements a web client that is represented as a trading terminal.<br>
With this interactive web client, you can select a stock symbol on the left side of the terminal on the **Symbols** panel (see the screenshot below).<br>
After selecting the symbol you can buy or sell the required amount of stocks by a specific price in the **BUY/SELL** panel.<br>
On the **Orders** panel, you can see all your open orders. You can cancel the order if you want by clicking the icon on the specific order.<br>
On the **Order book** panel you can see the book order of the selected stock symbol. There you can see prices and volumes for each price.<br>
On the **Logs** panel, you can see all logs that come from the backend server through a WebSocket connection.<br>
On the **Chart** panel in the middle, you can see the price history. It displays the real history of the selected stock. Right now only 5 seconds timeframe is supported<br>
By default clientId for the web application is "client1"<br>
* By default, the application is running on the 8081 port


**Prerequisites:**
- npm installed<br>
- nodejs installed<br>

**How to build and run:**
```
cd ./webclient/
npm install
npm run serve
```

## Screens
![Screen_1](https://github.com/BootGenius/StockMarketSimulator/raw/main/docs/screen_1.png)






