import httpClient from '../httpclient'

const getSymbols = () => {
    return httpClient.get('/symbols');
};

const getOrderBook = (symbol) => {
    let params = {
        symbol: symbol
    };
    return httpClient.get('/orderBook', {params});
};

const getPriceHistory = (symbol, priceHistoryType) => {
    let params = {
        symbol:  symbol,
        priceHistoryType: priceHistoryType
    };
    return httpClient.get('/priceHistory', {params});
};

const getOrders = (clientId, symbol) => {
    let params = {
        clientId:  clientId,
        symbol: symbol
    };
    return httpClient.get('/orders', {params});
};


const cancelOrder = (clientId, symbol, orderId) => {
    let formData = new FormData();
    formData.set('clientId', clientId);
    formData.set('symbol', symbol);
    formData.set('orderId', orderId);

    return httpClient.post('/cancelOrder', formData);
};

const addOrder = (clientId, symbol, type, quantity, price) => {
    let formData = new FormData();
    formData.set('clientId', clientId);
    formData.set('symbol', symbol);
    formData.set('type', type);
    formData.set('quantity', quantity);
    formData.set('price', price);

    return httpClient.post('/addOrder', formData);
};


export default {
    getSymbols,
    getOrders,
    getOrderBook,
    addOrder,
    cancelOrder,
    getPriceHistory
}