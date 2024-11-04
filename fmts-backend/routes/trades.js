const express = require('express');
const router = express.Router();
const tokenDao = require('../repositories/AccessTokensDAO');
const tradeDao = require('../repositories/tradeDAO');
const clientVerification = require('./clientVerification.js');

router.post('/trade', (request, response) => {
    let order = readOrder(request);
    let trade;

    if (tokenDao.checkToken(order.token)) {
        trade = tradeDao.performTrade(order);
        if(trade && trade.executionPrice == null)
            response.status(409).send(); //Send 409 if couldnt execute price
        else
            response.send(JSON.stringify(trade)); //IF trade is null returns it
    }
    // if (clientVerification.validateEmail(order.email)) {    // validate email
    //     let id = clientVerification.generateID(order);
    //     let token = clientVerification.generateToken(order.email);

    //     if (id == order.clientId && token == order.token) {
    //         trade = tradeDao.performTrade(order);
    //         response.send(JSON.stringify(trade));
    //     }
    else {
        response.status(406).send();
    }
});

function readOrder(request) {
    let order = new Object();
    order.instrumentId = request.body.instrumentId;
    order.quantity = request.body.quantity;
    order.targetPrice = request.body.targetPrice;
    order.direction = request.body.direction;
    order.clientId = request.body.clientId;
    order.orderId = request.body.orderId;
    order.token = request.body.token;

    return order;
}

router.get('/instruments/:category', (request, response, next) => {
    console.log('GET: Instruments request');

    let instrumentList = tradeDao.getInstruments(request.params.category);
    // reset token vaility for another 5 minutes
    tokenDao.updateTokenValidity(request.body.token)

    if (instrumentList.length == 0) {
        response.sendStatus(204);
    }
    response.end(JSON.stringify(instrumentList));
});

router.get('/instruments', (request, response, next) => {
    console.log('GET: Instruments request');

    let instrumentList = tradeDao.getInstruments();
    // reset token vaility for another 5 minutes
    tokenDao.updateTokenValidity(request.body.token)

    if (instrumentList.length == 0) {
        response.sendStatus(204);
    }
    response.end(JSON.stringify(instrumentList));
});

router.get('/prices/:category', (request, response, next) => {
    console.log('GET: Prices request');
    let prices = tradeDao.getPrices(request.params.category);
    if (prices.length == 0) {
        response.sendStatus(204);
    }

    response.end(JSON.stringify(prices));
});

router.get('/prices', (request, response, next) => {
    console.log('GET: Prices request');
    let prices = tradeDao.getPrices(request.query.category);
    if (prices.length == 0) {
        response.sendStatus(204);
    }

    response.end(JSON.stringify(prices));
});

router.post('/prices', (request, response, next) => {
    console.log('POST: Prices request');
    console.log(request.body.token);

    //console.log(request.body.token);
    if (tokenDao.checkToken(request.body.token) == true) {
        let prices = tradeDao.getPrices(request.query.category);

        // reset token vaility for another 5 minutes
        tokenDao.updateTokenValidity(request.body.token)

        if (prices.length === 0) {
            response.sendStatus(204);
        }

        response.end(JSON.stringify(prices));
    }
    else {
        response.sendStatus(401);
    }

});


module.exports = router;