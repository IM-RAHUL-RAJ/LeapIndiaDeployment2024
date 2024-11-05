package com.marshals.integration;

import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;

public interface ClientTradeDao {	
	
	//Trade history 
	TradeHistory getClientTradeHistory(String clientId); //Change return type to TradeHistory once the model is created
	//Add trade
	void addTrade(Trade trade);

}
