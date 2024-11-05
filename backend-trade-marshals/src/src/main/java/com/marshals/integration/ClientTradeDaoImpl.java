package com.marshals.integration;
 
import java.util.ArrayList;
import java.util.List;
 
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.integration.mapper.ClientTradeMapper;
 
@Repository("clientTradeDao")
public class ClientTradeDaoImpl implements ClientTradeDao {
	@Autowired
	 private ClientTradeMapper clientTradeMapper;

	@Autowired
	private Logger logger;

	@Override
	public TradeHistory getClientTradeHistory(String clientId) {
		List<Trade> trades = new ArrayList<>();
		try {
			logger.debug("enter");
			trades = clientTradeMapper.getClientTradeHistory(clientId);
			System.out.println("Trades when invalid clientId is passed -> " + trades + ", clientId -> " + clientId);
			if(trades.isEmpty()) {
//				throw new DatabaseException("Client ID does not exist");
				throw new DatabaseException("Client has no trades");
			}
			TradeHistory tradeHistory = new TradeHistory(clientId, trades);
			return tradeHistory;
		}catch(DatabaseException e) {
			logger.error("Error while executing get client trade history by client id ", e);
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void addTrade(Trade trade) {
		int orderRowcount = 0;
		int tradeRowcount = 0;
		try {
			logger.debug("enter");
			orderRowcount = clientTradeMapper.addOrder(trade.getOrder());
			tradeRowcount = clientTradeMapper.addTrade(trade);
			if(orderRowcount == 0) {
				throw new DatabaseException("couldn't add order to order table");
			}
			if(tradeRowcount == 0) {
				throw new DatabaseException("couldn't add trade to trade table");
			}
		} catch(DataAccessException e) {
		 	logger.error(e.getMessage());
			throw new DatabaseException(e.getMessage());
		} catch(DatabaseException e) {
			logger.error(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}
 
}