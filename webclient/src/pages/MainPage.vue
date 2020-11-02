<template>
    <div class="main-container overflow-y-hidden d-flex">
        <loading-panel :is-loading="isLoading" class="overflow-y-hidden d-flex">
            <panel-with-error :error-message="errorMessage" class="mt-1 d-flex overflow-y-hidden">
                <template slot="bottom-template">
                    <v-btn text class="justify-self-center" @click="loadItems">Try to refresh</v-btn>
                </template>

                <div class="page-container ma-5 d-flex flex-column grow overflow-y-hidden"
                     v-if="errorMessage == null">

                        <div class="d-flex flex-row overflow-y-hidden" style="height: 100%">
                            <symbol-list class="main-panel"
                                         :items="symbols"
                                         @selectedItemChanged="onSymbolSelected"
                            />

                            <operation-panel
                                    class="main-panel align-content-stretch"
                                    :symbol="selectedSymbol"
                            />

                            <trading-view-panel class="main-panel grow overflow-y-hidden grow"
                                                :symbol="selectedSymbol"
                                                :history="priceHistory"
                            />

                            <order-list class="main-panel grow overflow-y-hidden"
                                        @cancelOrder="onCancelOrder"
                                        :items="orders"/>

                            <order-book class="main-panel"
                                        :items="orderBook"
                            />
                        </div>
                    <log-panel
                            class="main-panel flex-grow-0"
                            :items="logs"
                    />
                </div>
            </panel-with-error>
        </loading-panel>
    </div>
</template>

<script>
    import {mapActions, mapState} from 'vuex';
    import SymbolList from "../components/SymbolList";
    import OrderBook from "../components/OrderBook";
    import LogPanel from "../components/LogPanel";
    import OrderList from "../components/OrderList";
    import OperationPanel from "../components/OperationPanel";
    import LoadingPanel from "../components/LoadingPanel";
    import PanelWithError from "../components/PanelWithError";
    import TradingViewPanel from "../components/TradingVuePanel";
    import createWebSocketClient from "../websocketclient";
    import _ from 'lodash';

    export default {
        name: "MainPage",

        components: {
            SymbolList,
            OrderBook,
            LogPanel,
            OrderList,
            OperationPanel,
            LoadingPanel,
            PanelWithError,
            TradingViewPanel
        },

        data: () => ({
            isLoading: false,
            errorMessage: null,
            selectedSymbol: null,
            logs: []
        }),

        computed: {
            ...mapState({
                symbols: state => state.main.symbols,
                orders: state => state.main.orders,
                orderBook: state => state.main.orderBook,
                priceHistory: state => state.main.priceHistory
            }),
        },

        methods: {
            ...mapActions({
                getSymbols: 'main/getSymbols',
                getOrders: 'main/getOrders',
                getOrderBook: 'main/getOrderBook',
                cancelOrder: 'main/cancelOrder',
                getPriceHistory: 'main/getPriceHistory'
            }),

            loadItems: async function () {
                this.isLoading = true;

                try {
                    await this.getSymbols();
                    this.errorMessage = null;
                } catch (error) {
                    console.log(error);
                    this.errorMessage = "An error occurred while getting data from the server.<br/> Please try again later. <br/>If this error occurs again please contact the developers";
                }
                this.isLoading = false;
            },

            onSymbolSelected(value) {
                this.selectedSymbol = value;
                if (!_.isEmpty(value)) {
                    this.getOrders({
                        symbol: this.selectedSymbol.name
                    });
                    this.getOrderBook({
                        symbol: this.selectedSymbol.name
                    });

                    this.getPriceHistory({
                        symbol: this.selectedSymbol.name,
                        priceHistoryType: 'SECOND5'
                    });
                }
            },

            onCancelOrder: async function (order) {
                this.isLoading = true;
                await this.cancelOrder({
                    symbol: order.symbol,
                    orderId: order.id,
                });
                this.isLoading = false;
            }
        },

        mounted() {
            this.$store.commit("main/updateClientId", process.env.VUE_APP_CLIENT_ID);
            this.loadItems();

            createWebSocketClient((stompClient) => {
                console.log("Connected");
                stompClient.subscribe("/topic/logs", (tick) => {
                    this.logs.push(tick.body);
                });

                stompClient.subscribe("/topic/orderBookChanged", (tick) => {
                    if (!_.isEmpty(this.selectedSymbol) && tick.body === this.selectedSymbol.name) {
                        this.getOrderBook({
                            symbol: this.selectedSymbol.name
                        });
                    }
                });

                stompClient.subscribe("/topic/priceHistoryChanged", (tick) => {
                    if (!_.isEmpty(this.selectedSymbol) && tick.body === this.selectedSymbol.name) {
                        this.getPriceHistory({
                            symbol: this.selectedSymbol.name,
                            priceHistoryType: 'SECOND5'
                        });
                    }
                });

                stompClient.subscribe(`/user/${this.$store.state.main.clientId}/queue/ordersChanged`, (tick) => {
                    if (!_.isEmpty(this.selectedSymbol) && tick.body === this.selectedSymbol.name) {
                        this.getOrders({
                            symbol: this.selectedSymbol.name
                        });
                    }
                });
            });
        }
    }

</script>

<style scoped>

    .main-panel {
        border: 1px solid black !important;
        margin: 5px;
    }

    .page-container {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        overflow-y: hidden !important;
        display: flex;
    }

    .main-container {
        background: #ececec;
        display: flex;
        flex-direction: column;
        position: absolute;
        overflow-y: hidden !important;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

</style>