package com.marshals.business.services;
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.marshals.business.Holding;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.business.TradePL;
import com.marshals.integration.ClientActivityReportDao;
import com.marshals.integration.DatabaseException;
 
@Service("activityReportService")
public class ActivityReportService {
	@Autowired
	private Logger logger;
	
	private ClientActivityReportDao dao;
	
	private TradeService tradeService; 

	public ActivityReportService() {}

	@Autowired
	public ActivityReportService(@Qualifier("clientActivityReportDao") ClientActivityReportDao activityDao, @Qualifier("tradeService") TradeService service) {
		//Initializing the reqd Daos and TradeHistory Service
		this.dao = activityDao;
		this.tradeService = service;
	}

	//Generate Report with Clients Holdings
	public List<Holding> generateHoldingsReport(String clientId) {
		List<Holding> holdings;
		try {
			holdings = dao.getClientHoldings(clientId);
			if(clientId == null) {
				throw new NullPointerException("Client Id should not be null for Holdings");
			}
		} catch(NullPointerException e) {
			logger.error(e.getMessage());
			throw e;
		} catch(DataAccessException e) {
			logger.error(e.getMessage());
			throw e;
		} catch(DatabaseException e) {
			logger.error(e.getMessage());
			throw e;
		}
		return holdings;
	}
	
	//Generate Report with Clients Trade History
	public TradeHistory generateTradeReport(String clientId) {
		//Return Trade History of given client's portfolio
		if(clientId == null) {
			throw new NullPointerException("Client Id should not be null for Trade History");
		}
		return tradeService.getClientTradeHistory(clientId);
	}
	
	//Generate Report with Clients P&L Data
	public List<TradePL> generatePLReport(String clientId) {
		//Make use of Trade History
		  if(clientId == null) {
			 throw new NullPointerException("Client Id should not be null to calculate Profit Loss");
		  }
		  
		  List<TradePL> profitLossList = new ArrayList<TradePL>();
		  Map<String, BigDecimal> profitLossMap = new HashMap<>();
	      Map<String, BigDecimal> buyPositions = new HashMap<>();
	      TradeHistory tradeHistory = tradeService.getClientTradeHistory(clientId);
 
	      for (Trade trade : tradeHistory.getTrades()) {
	            if (!trade.getClientId().equals(clientId)) continue;
	            String instrumentId = trade.getInstrumentId();
	            BigDecimal tradeValue = trade.getCashValue();
	            if (trade.getDirection().equals("B")) {
	                // Use BigDecimal's add method
	                buyPositions.put(instrumentId, tradeValue.add(buyPositions.getOrDefault(instrumentId, BigDecimal.ZERO)));
	            } else if (trade.getDirection().equals("S")) {
	                // Get the buyValue as BigDecimal
	                BigDecimal buyValue = buyPositions.getOrDefault(instrumentId, BigDecimal.ZERO);
	                // Calculate profit/loss using BigDecimal's subtract method
	                BigDecimal profitLoss = buyValue.subtract(tradeValue);
	                profitLossMap.put(instrumentId, profitLossMap.getOrDefault(instrumentId, BigDecimal.ZERO).add(profitLoss));
	                // After selling, we assume the position is cleared
	                buyPositions.remove(instrumentId);
	            }
	        }
	      for(Map.Entry<String, BigDecimal> entry: profitLossMap.entrySet()) {
	    	  TradePL tradePl = new TradePL(entry.getKey(), entry.getValue());
	    	  profitLossList.add(tradePl);
	      }
	      
	      return profitLossList;
	    }
	}