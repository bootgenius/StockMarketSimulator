import {Stomp} from "@stomp/stompjs/esm6/compatibility/stomp";

const createWebSocketClient = function (successCallback) {
    const stompClient = Stomp.over(() => {
        return new WebSocket(process.env.VUE_APP_API_WEBSOCKET_URL);
    });
    stompClient.debug = () => {
    };
    stompClient.reconnect_delay = 1000;

    stompClient.connect('', '', () => {
            console.log("WebSocket connected")
            successCallback(stompClient);
        },
        () => {
            console.log("WebSocket Error");
        },
        () => {
            console.log("WebSocket Disconnected");
        });
}

export default createWebSocketClient;