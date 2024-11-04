const fs = require('fs');

const pricesFile = './data/prices.json';
const instrumentsFile = './data/instruments.json';
const tolerance = 0.05; // 5%
const fee = 0.01;   // 1%

exports.getInstruments = (category) => {
    let instruments = JSON.parse(read_instruments_json_file());

    if (category !== undefined) {
        instruments = instruments.filter(instrument =>
            instrument.categoryId === category);
    }

    return instruments;
}

exports.getPrices = (category) => {
    let prices = loadAllPrices();

    if (category !== undefined) {
        prices = prices.filter(price =>
            price.instrument.categoryId === category);
    }

    return prices;
}

exports.performTrade = (order) => {
    let prices = loadAllPrices();
    let execPrice = null;
    let tradeObj = createTrade(order);

    // find ordered instrument
    let price = prices.find(
        price => price.instrument.instrumentId == order.instrumentId);

    //Check whether order details are valid
    if((order.direction !="S" && order.direction !="B") || price == undefined)
        return null    
    
    // check whether target price is within tolerance
    if (order.direction == "S") { // Sell
        if (order.targetPrice > price.bidPrice * (1.0 - tolerance) &&
            order.targetPrice < price.bidPrice * (1.0 + tolerance)) {
            execPrice = price.bidPrice;
        }
    } else if (order.direction == "B") {  // Buy
        if (order.targetPrice > price.askPrice * (1.0 - tolerance) &&
            order.targetPrice < price.askPrice * (1.0 + tolerance)) {
            execPrice = price.askPrice;
        }
    }

    if (execPrice != null) {
        tradeObj.executionPrice = execPrice;
        // add fee
        tradeObj.cashValue = order.quantity * execPrice * (1.0 + fee);
        tradeObj.tradeId = generateTradeId();

        return tradeObj;
    } else {
        tradeObj.executionPrice = execPrice
        return tradeObj;
    }
}

let createTrade = (order) => {
    let trade = new Object();
    trade.instrumentId = order.instrumentId;
    trade.quantity = order.quantity;
    trade.executionPrice = order.targetPrice;
    trade.direction = order.direction;
    trade.clientId = order.clientId;
    trade.order = order;
    trade.tradeId = "id";
    trade.cashValue = 42;

    return trade;
}

let generateTradeId = () => {
    let TID = generateAlphaNum() + "-" + generateAlphaNum() + "-"
        + generateAlphaNum();
    return TID; 
}

let generateAlphaNum = () => {
    return Math.random().toString(36).slice(2);
}

let loadAllPrices = () => {
    let instruments = JSON.parse(read_instruments_json_file());
    let prices = JSON.parse(read_prices_json_file());

    for (var i = 0; i < prices.length; ++i) {
        let price = prices[i];
        makePrice(price, instruments);
    }

    return prices;
}

let makePrice = (price, instruments) => {
    let ID = price.instrumentId;

    let instru = instruments.find(instrument => instrument.instrumentId === ID);

    if (instru != null && instru != undefined) {
        price.instrument = instru;
        delete price.instrumentId;
    }
}



let read_prices_json_file = () => {
    return fs.readFileSync(pricesFile);
}


let read_instruments_json_file = () => {
    return fs.readFileSync(instrumentsFile);
}