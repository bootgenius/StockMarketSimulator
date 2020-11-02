import stockMarketApi from '../../api/StockMarketApi'

// initial state
const state = () => ({
    symbols: [],
    orders: [],
    priceHistory: [],
    orderBook: [],
    clientId: null
});

// getters
const getters = {};

// actions
const actions = {

    async getPriceHistory({commit}, {symbol, priceHistoryType}) {
        try {
            let result = await stockMarketApi.getPriceHistory(symbol, priceHistoryType);
            commit('updatePriceHistory', result.data);
        } catch (error) {
            throw `Can not get price history`;
        }
    },

    async getSymbols({commit}) {
        try {
            let result = await stockMarketApi.getSymbols();
            commit('updateSymbols', result.data);
        } catch (error) {
            throw `Can not get list of symbols`;
        }
    },

    async getOrderBook({commit}, {symbol}) {
        try {
            let result = await stockMarketApi.getOrderBook(symbol);
            commit('updateOrderBook', result.data);
        } catch (error) {
            throw `Can not get order book`;
        }
    },

    async getOrders({commit, state}, {symbol}) {
        try {
            let result = await stockMarketApi.getOrders(state.clientId, symbol);
            commit('updateOrders', result.data);
        } catch (error) {
            throw `Can not get order list`;
        }
    },

    async addOrder({commit, state}, {symbol, type, quantity, price}) { // eslint-disable-line no-unused-vars
        try {
            await stockMarketApi.addOrder(state.clientId, symbol, type, quantity, price);
        } catch (error) {
            throw `An error occurred while adding new order: ${symbol} ${type} ${quantity} ${price}`;
        }
    },

    async cancelOrder({commit, state}, {symbol, orderId}) { // eslint-disable-line no-unused-vars
        try {
            await stockMarketApi.cancelOrder(state.clientId, symbol, orderId);
        } catch (error) {
            throw `An error occurred while cancelling order: ${symbol} ${orderId}`;
        }
    }
};

// mutations
const mutations = {
    updateSymbols(state, symbols) {
        state.symbols = symbols;
    },

    updateOrders(state, orders) {
        state.orders = orders;
    },

    updateOrderBook(state, orderBook) {
        state.orderBook = orderBook;
    },

    updateClientId(state, clientId) {
        state.clientId = clientId;
    },

    updatePriceHistory(state, priceHistory) {
        state.priceHistory = priceHistory;
    }

};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}