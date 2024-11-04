import { Router } from 'express';
import { TradeRestController } from '../controllers/trade-rest-controller';
 
const router = Router();
const tradeRestController = new TradeRestController();
 
// Define trade-specific routes
//Execute Trade
router.post('/execute-trade', async (req, res) => {
    await tradeRestController.executeTrade(req, res);
});
//Trade History
router.get('/trade-history/:clientId', async (req, res) => {
    await tradeRestController.getClientTradeHistoryByClientId(req, res);
});
//Get Live Prices
router.get('/live-prices', async (req, res) => {
    await tradeRestController.getLivePrices(req, res);
});
//Suggest Buy
router.post('/suggest-buy', async (req, res) => {
    await tradeRestController.getRoboAdvisorTopBuyInstruments(req, res);
});
//Suggest Sell
router.post('/suggest-sell', async (req, res) => {
    await tradeRestController.getRoboAdvisorTopSellInstruments(req, res);
});
 
 
export default router;