import axios from 'axios';
import { Request, Response } from 'express';
import defaultConfig from '../constants';
import { Trade } from '../models/Trade/trade';
import { Order } from '../models/Trade/order';
import { ClientPreferences } from '../models/Client/ClientPreferences';
 
export class TradeRestController {
 
    backendApiUrl: string;
    constructor() {
        this.backendApiUrl = `${defaultConfig.BACKEND_URL}/trade/`
    }
 
    //Execute Trade
    async executeTrade(req: Request, res: Response) {
        console.log(`Request for trade execution: ${JSON.stringify(req.body, null, 2)}`);
        const order: Order = req.body;
        if(order.clientId && order.direction && order.instrumentId && order.orderId
&& order.quantity && order.targetPrice && order.token
        ){
            try{
                const response = await axios.post(`${this.backendApiUrl}execute-trade`, order);
                console.log(`Response for Registering New Client: ${response}`);
                res.json(response.data) //Success 200 response
            } catch(err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while executing trade';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while executing trade',
                    });
                }
            }
        } else {
            res.status(400).json({ status: 400, message: 'Few order details are null' })
        }
    }
 
 
    //Get Client Trade History
    async getClientTradeHistoryByClientId(req: Request, res: Response) {
        console.log(`Request for get client trade history: ${JSON.stringify(req.params, null, 2)}`);
        // Read email from the URL
        const clientId = req.params.clientId;
        if (clientId) {
            try {
                const response = await axios.get(`${this.backendApiUrl}trade-history/${clientId}`);
                console.log(`Response for get client trade history: ${response}`);
                res.json(response.data) //Success 200 response
            } catch (err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    console.log('Error from backend while trying to fetch client trade history: ', err.response)
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while trying to fetch client trade history';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while trying to fetch client trade history',
                    });
                }
            }
        }
        else {
            res.status(400).json({ status: 400, message: 'clientId cannot be null' })
        }
    }
 
 
    //Get live prices
    async getLivePrices(req: Request, res: Response) {
        console.log(`Request for fetching live prices`);
        try {
            const response = await axios.get(`${this.backendApiUrl}live-prices`);
            console.log(`Response for get client live prices: ${response}`);
            res.json(response.data) //Success 200 response
        } catch (err) {
            if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                console.log('Error from backend while trying to fetch live prices: ', err.response)
                const { status, data } = err.response;
                const errorMessage = data.message || 'An error occurred while trying to fetch live prices';
                const errorStatus = status || 500;
                res.status(errorStatus).json({
                    status: errorStatus,
                    message: errorMessage,
                });
            } else {
                res.status(500).json({
                    status: 500,
                    message: 'Unexpected error in backend service while trying to fetch live prices',
                });
            }
        }
    }
 
    //Robo Advisor - Suggest Buy
    async getRoboAdvisorTopBuyInstruments(req: Request, res: Response) {
        console.log(`Request for fetching suggest buy: ${JSON.stringify(req.body, null, 2)}`);
        const clientPreferences: ClientPreferences = req.body;
        if(clientPreferences.acceptAdvisor && clientPreferences.clientId && clientPreferences.incomeCategory
&& clientPreferences.investmentPurpose && clientPreferences.lengthOfInvestment && clientPreferences.percentageOfSpend
&& clientPreferences.riskTolerance
        ){
            try{
                const response = await axios.post(`${this.backendApiUrl}suggest-buy`, clientPreferences);
                console.log(`Response for Fetching Robo advisor suggest buy: ${response}`);
                res.json(response.data) //Success 200 response
            } catch(err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while Fetching Robo advisor suggest buy ';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while Fetching Robo advisor suggest buy',
                    });
                }
            }
        } else {
            res.status(400).json({ status: 400, message: 'Few client preference details are null' })
        }
    }
 
    //Robo Advisor - Suggest Buy
    async getRoboAdvisorTopSellInstruments(req: Request, res: Response) {
        console.log(`Request for fetching suggest sell: ${JSON.stringify(req.body, null, 2)}`);
        const clientPreferences: ClientPreferences = req.body;
        if(clientPreferences.acceptAdvisor && clientPreferences.clientId && clientPreferences.incomeCategory
&& clientPreferences.investmentPurpose && clientPreferences.lengthOfInvestment && clientPreferences.percentageOfSpend
&& clientPreferences.riskTolerance
        ){
            try{
                const response = await axios.post(`${this.backendApiUrl}suggest-sell`, clientPreferences);
                console.log(`Response for Fetching Robo advisor suggest sell: ${response}`);
                res.json(response.data) //Success 200 response
            } catch(err) {
                if (axios.isAxiosError(err) && err.response) {  // Check if the error has a response object
                    const { status, data } = err.response;
                    const errorMessage = data.message || 'An error occurred while Fetching Robo advisor suggest sell';
                    const errorStatus = status || 500;
                    res.status(errorStatus).json({
                        status: errorStatus,
                        message: errorMessage,
                    });
                } else {
                    res.status(500).json({
                        status: 500,
                        message: 'Unexpected error in backend service while Fetching Robo advisor suggest sell',
                    });
                }
            }
        } else {
            res.status(400).json({ status: 400, message: 'Few client preference details are null' })
        }
    }
}