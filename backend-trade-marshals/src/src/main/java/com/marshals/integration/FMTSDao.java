package com.marshals.integration;

import java.util.List;

import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Trade;

public interface FMTSDao {

	//Related to client validation
	FMTSValidatedClient verifyClient(String email);  //For registration
	FMTSValidatedClient verifyClient(String email, String clientId); //For login
	
	//Related to trades
	List<Price> getLivePrices(); //For getting live prices
	Trade createTrade(Order order); //For executing trade
	
}
